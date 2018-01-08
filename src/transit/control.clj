;    Copyright (C) 2017-2018  Joseph Fosco. All Rights Reserved
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

(ns transit.control
  (:require
   [overtone.live :refer [now]]
   [transit.config.config :refer [get-setting set-setting]]
   [transit.ensemble.ensemble :refer [init-ensemble]]
   [transit.ensemble.ensemble-status :refer [start-status]]
   [transit.player.player :refer [create-player]]
   [transit.player.player-play-note :refer [play-next-note]]
   [transit.melody.melody-event :refer [create-melody-event]]
   [transit.util.log :as log]
   [transit.util.print :refer [print-banner]]
   )
  )

(defonce transit-started (atom false))

(defn init-transit
  "Initialize transit to play. Use only once (first time)

   args -
   players - list of all initial players
   melodies - list of all initial melodies (1 for each player)
   msgs -  an empty list to be used for msgs sent to the player
  "
  [players melodies msgs]
  (init-ensemble players melodies msgs)
  )

(defn new-player
  [player-id]
  (create-player :id player-id)
  )

(defn init-melody
  [player-id]

  (vector (create-melody-event :melody-event-id 0
                               :note nil
                               :dur-info nil
                               :volume nil
                               :instrument-info nil
                               :player-id player-id
                               :event-time 0
                               :structr-id nil
                               ))
  )

(defn- play-first-note
  [player-id]
  (play-next-note player-id (now))
  )

(defn- start-playing
  "calls play-note the first time for every player in ensemble"
  []
  (log/warn "********** start-playing ****************")
  (dotimes [id (get-setting :num-players)] (play-first-note id))
  )

(defn start-transit
  [& {:keys [num-players]}]
  (when num-players (set-setting :num-players num-players))
  (let [number-of-players (get-setting :num-players)
        init-players (map new-player (range number-of-players))
        init-melodies (map init-melody (range number-of-players))
        init-msgs (for [x (range number-of-players)] '())
        ]
    (set-setting :volume-adjust (min (/ 32 number-of-players) 1))
    (init-transit init-players init-melodies init-msgs)
    (when (not @transit-started)
      (start-status)
      (reset! transit-started true)
      )
    (start-playing)
    )
  )

(defn clear-transit
  []
  )

(defn pause-transit
  []
  )

(defn quit-transit
  []
  )
