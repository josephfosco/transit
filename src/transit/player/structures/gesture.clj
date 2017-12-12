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
   [transit.config.constants :refer [MIN-FREQ MAX-FREQ]]
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
                    gesture-type ;; FREE or METERED (METERED has mm and rhythmic values)
                    gesture-complete? ;; is this a complete gesture
                    gesture-next-event-ndx
                    gesture-last-melody-event
                    ])

(defn cleanup-gesture-event
  ;; Called when playing a gesture is interrupted by another structure
  [gesture]
  (if (= 0 (:gesture-next-event-ndx gesture))
    gesture
    (reset-internal-strength-to-orig gesture)
    )
  )

(defn cleanup-partial-gesture-event
  ;; Called when building a gesture is interrupted by another structure
  [gesture]
  (println "############### CLEANING PARTIAL GESTURE ####################")
  (assoc (-> (set-cleanup-fn gesture nil)
             (reset-internal-strength-to-orig))
         :gesture-events []
         :gesture-next-event-ndx nil
         :gesture-last-melody-event 0
         )
  )

(defn create-new-gesture-event
  [player gesture]
  (println "CREATING GESTURE EVENT")
  {:note (if (nil? (:gesture-nextevent-ndx gesture))
           (select-random-pitch-for-player player)
           (let [prior-note (:note
                             ((:gesture-events gesture)
                              (dec (:gesture-next-event-ndx gesture))))
                 ]
             (println "prior-note:" prior-note)
             (select-random-pitch-for-player
              player
              (max (int (/ prior-note 2)) MIN-FREQ)
              (min (* prior-note 2) MAX-FREQ)
              ))
           )
   :dur-info (select-random-rhythm GESTURE-MIN-MILLIS GESTURE-MAX-MILLIS)
   }
  )

(defn get-next-gesture-event
  " Returns an updated gesture and a melody-event based on the
    :gesture-next-event-ndx from :gesture-events"
  [player next-melody-id gesture]
  (println "!!!!!! get-next-gesture-event  !!!!!")
  (println "player-id: " (:id player))
  (println next-melody-id gesture)
  (let [next-gesture-event ((:gesture-events gesture)
                           (:gesture-next-event-ndx gesture))
        next-gesture-event-ndx (if (:gesture-complete? gesture)
                                 (mod (inc (:gesture-next-event-ndx gesture))
                                      (count (:gesture-events gesture)))
                                 (inc (:gesture-next-event-ndx gesture))
                                 )
        ]
    (println "@@@@@@ PLAYING GESTURE EVENT @@@@@@@@")
    [
     ;; After playing last note of gesture (next-gesture-event-ndx = 0 and
     ;;   :complete gesture) the cleanup-fn is set to nil (no cleanup needed)
     ;;   reset internal-strength to it's original value otherwise
     ;; When in the middle of a gesture, (next-gesture-event index not= 0 and
     ;;   :complete gesture), set the cleanup-fn and
     ;;   increase internal-strength for each note of gesture played
     ;; When in the middle of creating a gesture (next-gesture-event-index
     ;;   not= 0  and not :complete gesture) just increse internal-strength
     ;;   for each note of gesture played, cleanup-fn is already set
     (assoc (cond (and (=  0 next-gesture-event-ndx)
                       (:gesture-complete? gesture))
                  (-> (reset-internal-strength-to-orig gesture)
                      (set-cleanup-fn nil)
                      )
                  (:gesture-complete? gesture)
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
            :gesture-next-event-ndx
            next-gesture-event-ndx
            :gesture-last-melody-event
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
    :complete and :gesture-next-event-ndx updated
"
  [player gesture]
  (let [new-gesture-events-len (inc (count (:gesture-events gesture)))]
    (assoc (set-cleanup-fn gesture cleanup-partial-gesture-event)
           :gesture-events (assoc (:gesture-events gesture)
                                  (count (:gesture-events gesture))
                                  (create-new-gesture-event player gesture))
           :gesture-complete? (cond
                       (< new-gesture-events-len GESTURE-MIN-NOTES)
                       false
                       (= new-gesture-events-len GESTURE-MAX-NOTES)
                       true
                       (< (rand-int 2) 1)
                       true
                       :else
                       false
                       )
           :gesture-next-event-ndx (if (:gesture-next-event-ndx gesture)
                                     (:gesture-next-event-ndx gesture)
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
           :gesture-complete? true
           :gesture-next-event-ndx 0)
    gesture
    )
  )

(defn get-gesture-melody-event
  "Returns a vector consisting of the gesture (possibly updated) and
   a melody event (or nil for no melody event found/created)"
  [ensemble player melody player-id gesture next-melody-id]
  (if (:gesture-complete? gesture)
    (do
      (println "%%%%%%%%  Playing Existing Gesture  %%%%%%%%%%")
      (let [new-gesture (if (and (not= 0 (:gesture-next-event-ndx gesture))
                                 (not=
                                  next-melody-id
                                  (inc (:gesture-last-melody-event gesture)))
                                 )
                          ;; last melody event was not from gesture,
                          ;; start gesture from beginning
                          (assoc (reset-internal-strength-to-orig gesture)
                                 :gesture-next-event-ndx 0)
                          gesture
                          )]
        (get-next-gesture-event player next-melody-id new-gesture)
        ))
    ;; If there are gesture events, but the gesture is not complete,
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
        (if (:gesture-complete? new-gesture)
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
             gesture-type
             gesture-complete?] :or
      {internal-strength 0
       external-strength 0
       gesture-events []
       gesture-complete? false
       }}]
  (Gesture. (create-base-structure :structr-id structr-id
                                   :internal-strength internal-strength
                                   :external-strength external-strength
                                   :melody-fn get-gesture-melody-event)
            gesture-events
            gesture-type
            gesture-complete?
            nil  ;; gesture-next-event-ndx
            0  ;; gesture-last-melody-event
            )
  )
