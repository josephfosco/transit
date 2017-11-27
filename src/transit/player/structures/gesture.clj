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
   [transit.player.structures.base-structure :refer [create-base-structure
                                                     get-internal-strength
                                                     set-internal-strength
                                                     struct-updated]]
   )
  )


(def ^:private GESTURE-MIN-NOTES 2)
(def ^:private GESTURE-MAX-NOTES 4)
(def ^:private GESTURE-MAX-SKIP 12)
(def ^:private GESTURE-MAX-NUM-SKIPS 3)
(def ^:private GESTURE-MIN-MILLIS 200)
(def ^:private GESTURE-MAX-MILLIS 2000)

(defrecord Gesture [base
                    gesture-events
                    type  ;; FREE or METERED (METERED has mm and rhythmic values)
                    complete? ;; is this a complete gesture
                    next-gesture-event-ndx
                    last-gesture-melody-event
                    orig-internal-strength
                    ])

(defn get-next-gesture-event
  [player next-melody-id gesture]
  (println "!!!!!! get-next-gesture-event  !!!!!")
  (println next-melody-id gesture)
  (let [next-gesture-event ((:gesture-events gesture)
                            (:next-gesture-event-ndx gesture))]
    (println "@@@@@@ PLAYING GESTURE EVENT @@@@@@@@")
    [
     ;; increase internal-strength for each note of gesture played
     ;; after playing last note of gesture reset internal-strength
     ;; to it's original value
     (assoc (set-internal-strength
             gesture
             (if (< (:next-gesture-event-ndx gesture)
                    (count (:gesture-events gesture)))
               (* 3 (get-internal-strength gesture))
               (:orig-internal-strength gesture)
               )

             )
            :next-gesture-event-ndx
            (mod (inc (:next-gesture-event-ndx gesture))
                 (count (:gesture-events gesture)))
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
                          )
     ]
    )
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
    (assoc (struct-updated gesture)
           :gesture-events gestr
           :complete true
           :next-gesture-event-ndx 0)
    gesture
    )
  )

(defn get-gesture-melody-event
  "Returns a vector consisting of the gesture (possibly updated) and
   a melody event (or nil for no melody event found/created)"
  [ensemble player melody player-id gesture next-melody-id]
  (if (= (:gesture-events gesture) nil)
    (let [new-gesture (complete-gesture-struct gesture melody)]
      (if (not (nil? (:next-gesture-event-ndx new-gesture)))
        (do
          (println "$$$$$$ found-gesture " (:gesture-events new-gesture))
          (println "&&& new-gesture: " new-gesture)
          (get-next-gesture-event player next-melody-id new-gesture)
          )
        [gesture nil]
        )
      )
    (do
      (println "%%%%%%%%  Playing Existing Gesture  %%%%%%%%%%")
      (let [new-gesture (if (not= next-melody-id
                                  (inc (:last-gesture-melody-event gesture)))
                          ;; last melody event was not from gesture,
                          ;; start gesture from beginning
                          (assoc gesture :next-gesture-event-ndx 0)
                          gesture
                          )]
        (get-next-gesture-event player next-melody-id new-gesture)
        ))
    )
  )

(defn create-gesture-struct
  [& {:keys [struct-id
             internal-strength
             external-strength
             gesture-events
             type
             complete?] :or
      {internal-strength 0
       external-strength 0
       melody-events []
       complete? false
       }}]
  (Gesture. (create-base-structure :struct-id struct-id
                                   :internal-strength internal-strength
                                   :external-strength external-strength
                                   :melody-fn get-gesture-melody-event)
          gesture-events
          type
          complete?
          nil  ;; next-gesture-event-ndx
          0  ;; last-gesture-melody-event
          internal-strength  ;; orig-internal-strength
          )
  )
