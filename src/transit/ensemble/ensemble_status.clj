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
                                        get-event-time-from-melody-event
                                        get-play-time-from-melody-event
                                        ]]
   [transit.util.util :refer [msgs-pub]]
   )
  )

;; note-times is a vector of lists. Each list is the time and dur-millis
;; of a note that was played.
(def ^:private note-times (atom []))
;; note-times-millis is the number of millis to store info in notetimes
(def ^:private note-times-millis 2000)

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

(defn event-expired?
  ;; note time is expired if it ended 2 seconds or more before now
  [note-time]
  (let [earliest-time (- (System/currentTimeMillis) note-times-millis)
        end-time (+ (first note-time) (second note-time))
        ]
    (if (< end-time earliest-time) true false)
    )
  )

(defn add-event-to-note-times
  [cur-note-times new-note-time]
  ;; remove note-times that occured more than 2 secs ago and add new event
  (conj (vec (drop-while event-expired? cur-note-times)) new-note-time)
  )

(defn new-melody-event
  [melody-event]
  (let [play-time (get-play-time-from-melody-event melody-event)]
    (swap! note-times
           add-event-to-note-times
           (list play-time
                 (- (get-dur-millis-from-melody-event melody-event)
                    (- play-time
                       (get-event-time-from-melody-event melody-event))
                    )
                 )
           )
    )
  )

(defn get-note-dur-list
  " Returns a list of note-durs starting at from-time up til to-time

    cur-note-times - a list of lists of note-start-times and durations
    from-time - the earliest time (in millis) to return a duration for
    to-time - the latest time (in millis) to return a duration for"
  [cur-note-times from-time to-time]
  (for [note-info cur-note-times
        :let [offset (if (< (first note-info) from-time)
                       (- from-time (first note-info))
                       0
                       )
              note-time (+ (first note-info) offset)
              note-dur (max 0 (- (second note-info) offset))
              dur (if (> (+ note-time note-dur) to-time)
                    (- to-time note-time)
                    note-dur)]]
    dur)
  )

(defn get-ensemble-density-ratio
  "Returns ratio of time sound is present to total time, in the preceeding
   note-times-millis"
  []

  (let [cur-note-times @note-times
        cur-time (System/currentTimeMillis)
        from-time (- cur-time note-times-millis)
        total-note-time (apply + (get-note-dur-list cur-note-times
                                                    from-time
                                                    cur-time))
        ]
    (/ total-note-time (* note-times-millis (get-setting :num-players)))
    )
 )

(defn get-ensemble-density
  "Returns a density value between 0 and 10"
  []
  (Math/round (float (* 10 (get-ensemble-density-ratio))))
 )

(defn start-ensemble-status
  []
  (reset! note-times [])

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
