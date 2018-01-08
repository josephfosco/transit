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

(ns transit.ensemble.ensemble
  (:require
   [transit.config.config :refer [get-setting]]
   [transit.melody.melody-event :refer [print-melody-event]]
   [transit.player.player :refer [print-player]]
   [transit.util.log :as log]
   )
  )

(def ^:private ensemble (atom nil))

(defn get-ensemble
  []
  @ensemble)

(defn get-melody
  [ensemble player-id]
  ((:melodies ensemble) player-id)
  )

(defn get-player
  [ensemble player-id]
  ((:players ensemble) player-id)
  )

(defn player-and-melody-update
  [ens player melody player-id]
  (assoc ens
         :players (assoc (:players ens) player-id player)
         :melodies (assoc (:melodies ens) player-id melody)
         :msgs (:msgs ens)
         )
  )

(defn update-player-and-melody
  [player melody player-id]
  (swap! ensemble player-and-melody-update player melody player-id)
  )

(defn reset-player-id-msgs
  [ens player-id]
  (assoc ens
         :players (:players ens)
         :melodies (:melodies ens)
         :msgs (assoc (:melodies ens) player-id '())
         )
  )

(defn try-to-clear-msgs-for-player-id
  [player-id]
  (let [ens @ensemble]
    (if (compare-and-set! ensemble
                          ens
                          (reset-player-id-msgs ens player-id))
      ens
      (do
        (log/warn "*** Couldn't clear msgs for player " player-id " - Retrying .... ***")
        nil
        )
      )
    )
  )

(defn get-ensemble-clear-msg-for-player-id
  [player-id]
  (first
   (remove nil?
           (repeatedly (partial try-to-clear-msgs-for-player-id player-id))))
  )

(defn init-ensemble
  [init-players init-melodies init-msgs]
  (reset!
   ensemble
   {:players
    (into [] init-players)
    :melodies
    (into [] init-melodies)
    :msgs
    (into [] init-msgs)
    }
   )
  @ensemble
  )

(defn print-ensemble
  ([] (print-ensemble @ensemble))
  ([ensemble]
   (doseq [player (:players ensemble)]
     (print-player player)
     )
   )
  )

(defn print-player-id
 [player-id]
 (print-player (get-player @ensemble player-id))
 )

(defn print-melody-for-player-id
  [player-id]
  (let [plyr-melody ((:melodies @ensemble) player-id)]
    (doseq [melody-event plyr-melody]
      (print-melody-event melody-event))
    )
  )
