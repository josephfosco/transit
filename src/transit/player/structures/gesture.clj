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
   [transit.player.structures.base-structure :refer [create-base-structure]]
   )
  )


(def ^:private GESTURE-MIN-LEN 2)
(def ^:private GESTURE-MAX-LEN 12)
(def ^:private GESTURE-MAX-SKIP 12)
(def ^:private GESTURE-MAX-NUM-SKIPS 3)
(def ^:private GESTURE-MIN-MILLIS 200)
(def ^:private GESTURE-MAX-MILLIS 1000)

(defrecord Gesture [base
                    gesture-events
                    type  ;; FREE or METERED (METERED has mm and rhythmic values)
                    complete? ;; is this a complete gesture
                    ])

(defn find-gesture
 " Looks through past melody events for material that can be used to
   create a gesture"
  [melody]
  (let [possible-gesture
        (for [event (reverse melody) :while (not= nil (:note event))]
          {:note (:note event) :dur-info (:dur-info event)})]
    (if (>= (count possible-gesture) GESTURE-MIN-LEN)
      possible-gesture
      nil
      )
    )
 )

(defn next-melody-event
  [ensemble player melody player-id gesture next-id]
  (if-let [gesture (find-gesture melody)]
    (do
      (println "$$$$$$ found-gesture " gesture)
      (create-melody-event
       :id next-id
       :note 72
       :dur-info (select-random-rhythm)
       :volume 0.7
       :instrument-info (:instrument-info player)
       :player-id (:id player)
       :event-time nil
       ))
    (create-melody-event
     :id next-id
     :note 60
     :dur-info (select-random-rhythm)
     :volume 0.7
     :instrument-info (:instrument-info player)
     :player-id (:id player)
     :event-time nil
     )
    )
  )

(defn get-gesture-melody-event
  [ensemble player melody player-id gesture next-id]
  (println "##### get-gesture-melody-event #####")
  (next-melody-event ensemble player melody player-id gesture next-id)
  )

(defn create-gesture
  [& {:keys [internal-strength
             external-strength
             gesture-events
             type
             complete?] :or
      {internal-strength 0
       external-strength 0
       melody-events []
       complete? false
       }}]
  (Gesture. (create-base-structure :internal-strength internal-strength
                                 :external-strength external-strength
                                 :melody-fn get-gesture-melody-event)
          gesture-events
          type
          complete?
          )
  )
