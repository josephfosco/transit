;    Copyright (C) 2017  Joseph Fosco. All Rights Reserved
;
;    This program is free software: you can redistribute it and/or modify
;    it under the terms of the GNU General Public License as published by
;    the Free Software Foundation, either version 3 of the License, or
;    (at your option) any later version.
;
;    This program is distributed in the hope that it will be useful,
;    but WITHOUT ANY WARRANTY; without even the implied warranty of
;    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;    GNU General Public License for more details.
;
;    You should have received a copy of the GNU General Public License
;    along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns transit.player.structures.gesture
  (:require
   [transit.melody.melody-event :refer [create-melody-event]]
   [transit.melody.rhythm :refer [select-random-rhythm]]
   [transit.player.player-utils :refer [select-random-pitch-for-player]]
   [transit.player.structures.base-structure :refer [create-base-structure
                                                     get-internal-strength
                                                     get-structr-id
                                                     reset-internal-strength-to-orig
                                                     set-cleanup-fn
                                                     set-internal-strength
                                                     structure-updated]]
   )
  )


(def ^:private GESTURE-MIN-NOTES 2)
(def ^:private GESTURE-MAX-NOTES 4)
(def ^:private GESTURE-MAX-SKIP 12)
(def ^:private GESTURE-MIN-MILLIS 200)
(def ^:private GESTURE-MAX-MILLIS 2000)

(defrecord Gesture [base
                    gesture-events
                    type  ;; FREE or METERED (METERED has mm and rhythmic values)
                    complete? ;; is this a complete gesture
                    next-gesture-event-ndx
                    last-gesture-melody-event
                    ])

(defn cleanup-gesture-event
  ;; Called when playing a gesture is interrupted by another structure
  [gesture]
  (if (= 0 (:next-gesture-event-ndx gesture))
    gesture
    (reset-internal-strength-to-orig gesture)
    )
  )

(defn cleanup-partial-gesture-event
  ;; Called when building a gesture is interrupted by another structure
  [gesture]
  (println "#############################################################3333")
  (println "#############################################################3333")
  (println "############### CLEANING PARTIAL GESTURE ####################")
  (assoc (-> (set-cleanup-fn gesture nil)
             (reset-internal-strength-to-orig))
         :gesture-events []
         :next-gesture-event-ndx nil
         :last-gesture-melody-event 0
         )
  )

(defn create-new-gesture-event
  [player gesture]
  (println "CREATING GESTURE EVENT")
  {:note (select-random-pitch-for-player player)
   :dur-info (select-random-rhythm GESTURE-MIN-MILLIS GESTURE-MAX-MILLIS)
   }
  )

(defn get-next-gesture-event
  " Returns an updated gesture and a melody-event"
  [player next-melody-id gesture]
  (println "!!!!!! get-next-gesture-event  !!!!!")
  (println "player-id: " (:id player))
  (println next-melody-id gesture)
  (let [next-gesture-event ((:gesture-events gesture)
                           (:next-gesture-event-ndx gesture))
        next-gesture-event-ndx (if (:complete? gesture)
                                 (mod (inc (:next-gesture-event-ndx gesture))
                                      (count (:gesture-events gesture)))
                                 (inc (:next-gesture-event-ndx gesture))
                                 )
        ]
    (println "@@@@@@ PLAYING GESTURE EVENT @@@@@@@@")
    [
     ;; after playing last note of gesture (next-gesture-event-ndx = 0)
     ;; the cleanup-fn is set to nil (no cleanup needed)
     ;; reset internal-strength to it's original value otherwise
     ;; increase internal-strength for each note of gesture played
     (assoc (cond (and (=  0 next-gesture-event-ndx)
                       (:complete? gesture))
                  (-> (reset-internal-strength-to-orig gesture)
                      (set-cleanup-fn nil)
                      )
                  (:complete? gesture)
                  (-> (set-internal-strength
                       gesture
                       (* 2 (get-internal-strength gesture)))
                      (set-cleanup-fn cleanup-gesture-event)
                      )
                  :else
                  (-> (set-internal-strength
                       gesture
                       (* 2 (get-internal-strength gesture)))
                      )
              )
            :next-gesture-event-ndx
            next-gesture-event-ndx
            :last-gesture-melody-event
            next-melody-id
            )
     (create-melody-event :melody-event-id next-melody-id
                          :note (:note next-gesture-event)
                          :dur-info (:dur-info next-gesture-event)
                          :volume 0.7
                          :instrument-info (:instrument-info player)
                          :player-id (:id player)
                          :event-time nil
                          :structr-id (get-structr-id gesture)
                          )
     ]
    )
  )

(defn create-gesture-event
  " Returns an updated gesture with :gesture-events and possibly
    :complete and :next-gesture-event-ndx updated
"
  [player gesture]
  (let [new-gesture-events-len (inc (count (:gesture-events gesture)))]
    (assoc (set-cleanup-fn gesture cleanup-partial-gesture-event)
           :gesture-events (assoc (:gesture-events gesture)
                                  (count (:gesture-events gesture))
                                  (create-new-gesture-event player gesture))
           :complete? (cond
                       (< new-gesture-events-len GESTURE-MIN-NOTES)
                       false
                       (= new-gesture-events-len GESTURE-MAX-NOTES)
                       true
                       (< (rand-int 2) 1)
                       true
                       :else
                       false
                       )
           :next-gesture-event-ndx (if (:next-gesture-event-ndx gesture)
                                     (:next-gesture-event-ndx gesture)
                                     0
                                     )
           ))
  )

(defn find-gesture
 " Looks through past melody events for material that can be used to
   create a gesture"
  [melody]
  (let [possible-gesture
        (for [event (reverse melody) :while (not= nil (:note event))]
          {:note (:note event) :dur-info (:dur-info event)})]
    (cond (<= GESTURE-MIN-NOTES (count possible-gesture)  GESTURE-MAX-NOTES)
          (vec (reverse possible-gesture))
          (> (count possible-gesture)  GESTURE-MAX-NOTES)
          (let [ges-len (inc (rand-int GESTURE-MAX-NOTES))
                ges-start (- (rand-int (count possible-gesture)) ges-len)
                ]
            (vec (reverse (take ges-len (nthnext ges-start))))
            )
          :else
          nil
      )
    )
  )

(defn complete-gesture-struct
  [gesture melody]
  (if-let [gestr (find-gesture melody)]
    (assoc (structure-updated gesture)
           :gesture-events gestr
           :complete? true
           :next-gesture-event-ndx 0)
    gesture
    )
  )

(defn get-gesture-melody-event
  "Returns a vector consisting of the gesture (possibly updated) and
   a melody event (or nil for no melody event found/created)"
  [ensemble player melody player-id gesture next-melody-id]
  (if (:complete? gesture)
    (do
      (println "%%%%%%%%  Playing Existing Gesture  %%%%%%%%%%")
      (let [new-gesture (if (and (not= 0 (:next-gesture-event-ndx gesture))
                                 (not=
                                  next-melody-id
                                  (inc (:last-gesture-melody-event gesture)))
                                 )
                          ;; last melody event was not from gesture,
                          ;; start gesture from beginning
                          (assoc (reset-internal-strength-to-orig gesture)
                                 :next-gesture-event-ndx 0)
                          gesture
                          )]
        (get-next-gesture-event player next-melody-id new-gesture)
        ))
    ;; I there are gesture events, but the gesture is not complete,
    ;; add a new event.
    ;; If there are no geture events, attempt to build a gwsture from
    ;; prior melody events.
    ;; If it is not possible to build a complete gesture, start building
    ;; a new gesture.
    (if (> (count (:gesture-events gesture)) 0)
      (do
        (println "CONTINUING TO BUILD GESTURE!!!!")
        (get-next-gesture-event player
                                next-melody-id
                                (create-gesture-event player gesture))
        )
      (let [new-gesture (complete-gesture-struct gesture melody)]
        (if (:complete? new-gesture)
          (do
            (println "$$$$$$ found-gesture " (:gesture-events new-gesture))
            (println "&&& new-gesture: " new-gesture)
            (get-next-gesture-event player next-melody-id new-gesture)
            )
          (do
            (println "NO MATCHING GESTURE!!!!")
            (get-next-gesture-event player
                                    next-melody-id
                                    (create-gesture-event player gesture))
            )
          )
        )
      )
    )
  )


(defn create-gesture-struct
  [& {:keys [structr-id
             internal-strength
             external-strength
             gesture-events
             type
             complete?] :or
      {internal-strength 0
       external-strength 0
       gesture-events []
       complete? false
       }}]
  (Gesture. (create-base-structure :structr-id structr-id
                                   :internal-strength internal-strength
                                   :external-strength external-strength
                                   :melody-fn get-gesture-melody-event)
            gesture-events
            type
            complete?
            nil  ;; next-gesture-event-ndx
            0  ;; last-gesture-melody-event
            )
  )
