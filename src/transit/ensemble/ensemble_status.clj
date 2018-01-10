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

(ns transit.ensemble.ensemble-status
  (:require
   [clojure.core.async :refer [<! chan go-loop sub]]
   [transit.config.config :refer [get-setting]]
   [transit.melody.melody-event :refer [get-dur-millis-from-melody-event
                                        get-play-time-from-melody-event
                                        ]]
   [transit.util.util :refer [msgs-pub]]
   )
  )

(def ^:private note-times (atom '()))

(defrecord EnsembleStatus [])

(defn create-ensemble-status
  [& {:keys [num-players-counted
             keys
             scales
             num-loud
             num-soft
             num-fast
             num-slow
             num-melody
             num-sustained
             num-rhythmic
             ]
      }]
  (EnsembleStatus.
   )
  )

(defn add-event-to-note-times
  [cur-note-times event]
  (conj cur-note-times event)
  )

(defn new-melody-event
  [melody-event]
  (swap! note-times
         add-event-to-note-times
         (list (get-play-time-from-melody-event melody-event)
               (get-dur-millis-from-melody-event melody-event)
               )
         )
  )

(defn start-status
  []
  (def status-out-channel (chan (* 2 (get-setting :num-players))))
  (sub msgs-pub :melody-event status-out-channel)
  (go-loop [full-msg (<! status-out-channel)]
    (when full-msg
      (let [{:keys [data]} full-msg]
        (new-melody-event data)
        (recur (<! status-out-channel))
        ))
    )
  )
