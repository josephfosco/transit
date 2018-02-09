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

(ns transit.player.player
  (:require
   [transit.player.player-methods :refer [listen
                                          monitor-silence
                                          select-key
                                          select-mm
                                          select-instrument-for-player
                                          select-scale
                                          ]]
   [transit.player.player-utils :refer [create-random-rest-melody-event
                                        get-player-id]]
   [transit.player.structures.base-structure :refer [get-strength-fn]]
   )
  (:import transit.player.player_methods.MethodInfo)
  )

(defrecord Player [id
                   key
                   scale
                   mm
                   instrument-info
                   methods  ;; a vector of methods to possibly run
                   sampled-melodies
                   structures  ;; a vector of structures built or being built
                   next-struct-num
                   can-schedule? ;; is not waiting for msg(s)
                   ])

(defn get-initial-player-methods
  []
  (let [time (System/currentTimeMillis)]
      [
       (MethodInfo. listen 8 time)
       (MethodInfo. monitor-silence 1 time)
       (MethodInfo. select-key 1 time)
       (MethodInfo. select-mm 1 time)
       (MethodInfo. select-instrument-for-player 3 time)
       (MethodInfo. select-scale 1 time)
       ]
    )
  )

(defn create-player
  [& {:keys [:id]}]
  (Player. id
           nil  ;; key
           nil  ;; scale
           nil  ;; mm
           nil  ;; instrument
           (get-initial-player-methods)
           nil  ;; sampled-melodies
           []   ;; structures
           0    ;; next-struct-num
           true ;; can-schedule?
           )
  )

(defn get-player-instrument-info
  [player]
  (:instrument-info player))

(defn find-high-strength
  " A reducing function to find the index of the structr with the highest strength
    Calls strength-fn from structr and if it is > then the current highest
      strength (returns a result containing information for the current structr
    rslt is an array of 3 elements
      [ index of structure with the highest strength so far
        value of highest strength so far
        index for the next structure
      ]
  "
  [rslt structr]
  (let [new-strength ((get-strength-fn structr) structr)]
    (if (< (second rslt) new-strength)
      [(rslt 2) new-strength (inc (rslt 2))]
      [(first rslt) (second rslt) (inc (rslt 2))]
      )
    )
  )
