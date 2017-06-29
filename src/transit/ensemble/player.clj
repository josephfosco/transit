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

(ns transit.ensemble.player
  (:require
   [transit.ensemble.ensemble :refer [get-player]]
   [transit.ensemble.player-methods :refer [listen monitor-silence
                                            play-random-note select-instrument]]
   )
  )

(defrecord Player [id methods])

(defn create-player
  [& {:keys [:id]}]
  (Player. id
           [[play-random-note 1] [select-instrument 1] [monitor-silence 1]
             [listen 1]
             ]
           )
  )

(defn is-playing?
 "Returns:
   true - if player is playing now
   false - if player is not laying now
 "
 [player]
 )

(defn select-method
  [player]
  (let [methods (:methods player)]
    (first (get methods (rand-int (count methods))))
    )
  )

(defn run-player-method
  [ensemble player-id]
  (let [player (get-player ensemble player-id)]
    ((select-method player) player)
    )
  )

(defn play-note
  [ensemble player-id event-time]
  (println player-id event-time)
  (run-player-method ensemble player-id)
 )
