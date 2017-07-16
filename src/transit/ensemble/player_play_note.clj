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

(ns transit.ensemble.player-play-note
  (:require
   [transit.ensemble.ensemble :refer [get-melody get-player
                                      update-player-and-melody]]
   [transit.util.random :refer [weighted-choice]]
   [transit.util.util :refer [remove-element-from-vector]]
   )
  )

(defn get-player-method
  [player ndx]
  (first ((:methods player) ndx))
  )

(defn is-playing?
 "Returns:
   true - if player is playing now
   false - if player is not laying now
 "
 [player]
  )

(defn select-method
  " Returns the ndx into player-methods of the method to run "
  [player]
  (weighted-choice (mapv second (:methods player)))
  )

(defn run-player-method
  " Selects and executes one of the player :methods
    Returns player after executing method with the selected
      method removed from :methods
  "
  [player]
  (let [method-ndx (select-method player)]
    (->
     ((get-player-method player method-ndx) player)
     (assoc :methods
            (remove-element-from-vector (:methods player) method-ndx))
     )
    )
  )

(defn play-next-note
  [ensemble player-id event-time]
  (let [player (get-player ensemble player-id)
        melody (get-melody ensemble player-id)
        ]
    (->
     (run-player-method player)
     (update-player-and-melody {} player-id)
     )
    )
  (println (- (System/currentTimeMillis) event-time))
 )
