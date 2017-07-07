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
   [transit.config.config :refer [get-setting]]
   )
  )

(def ensemble (atom nil))

(defn get-melody
  [ensemble player-id]
  (get (:melodies ensemble) player-id)
  )

(defn get-player
  [ensemble player-id]
  (get (:players ensemble) player-id)
  )

(defn player-and-melody-update
  [ens player melody player-id]
  (assoc ens
         :players (assoc (:players ens) player-id player)
         :melodies (assoc (:melodies ens) player-id melody)
         )
  )

(defn update-player-and-melody
  [player melody player-id]
  (swap! ensemble player-and-melody-update player melody player-id)
 )

(defn init-ensemble
  [new-players]
  (reset!
   ensemble
   {:players
    (into {} (for [player new-players] [(:id player) player]))
    :melodies
    (zipmap (range (get-setting :num-players)) (repeat {}))
    }
   )
  @ensemble
  )

(defn print-ensemble
  [ensemble]

  )
