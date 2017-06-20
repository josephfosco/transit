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

(ns transit.ensemble.ensemble
  (:require
   [transit.ensemble.player :refer [create-player]]
   )
  )

(defn new-player
  [player-id]
  (create-player player-id)
  )

(defn init-ensemble
  [num-players]
  {:players (zipmap (range num-players)
                    (for [id (range num-players)] (create-player :id id)))
   }
  )

(defn print-ensemble
  [ensemble]

  )
