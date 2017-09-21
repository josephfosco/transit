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

(ns transit.player.structures.random-event
  (:require
   [transit.melody.dur-info :refer [create-dur-info]]
   [transit.melody.melody-event :refer [create-melody-event]]
   [transit.player.structures.base-structure :refer [create-base-structure
                                                     get-base-strength
                                                     ]]
   )
)

(defrecord RandomEvent [base
                        strength-fn
                        melody-fn
                        pitch
                        duration
                        volume
                        ])

(defn get-strength
  [rnd-evnt]
  (get-base-strength (:base rnd-evnt))
  )

(defn get-melody-event
  [player rnd-evnt next-id]
  (create-melody-event
   :id next-id
   :note 60
   :dur-info (create-dur-info :dur-millis 500)
   :volume 0.7
   :instrument-info (:instrument-info player)
   :player-id (:id player)
   :event-time nil
   )
  )

(defn create-random-event
  [& {:keys [melody-event-ids
             type
             complete?
             internal-strength
             external-strength] :or
      {melody-event-ids nil
       complete? false
       internal-strength 0
       external-strength 0}}]
  (RandomEvent. (create-base-structure internal-strength external-strength)
                 get-strength
                 get-melody-event
                 nil
                 nil
                 nil
                 )
  )
