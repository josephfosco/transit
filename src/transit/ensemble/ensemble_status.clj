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

(ns transit.ensemble.ensemble-status
  (:require
   [clojure.core.async :refer [<! chan go-loop sub]]
   [transit.util.util :refer [status-pub]]
   )
  )

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

(defn start-status
  []
  (def status-out-channel (chan))
  (sub status-pub :melody-event status-out-channel)
  (go-loop [full-msg (<! status-out-channel)]
    (when full-msg
      (let [{:keys [msg]} full-msg]
        (println  "#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#%#")
        (println msg)
        (recur (<! status-out-channel))
        ))
    )
  )
