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

(ns transit.player.player-utils
  (:require
   [transit.melody.melody-event :refer [create-melody-event
                                        get-structr-id-from-melody-event]]
   [transit.melody.pitch :refer [select-random-pitch]]
   [transit.melody.rhythm :refer [select-random-rhythm]]
   [transit.player.structures.base-structure :refer [get-cleanup-fn
                                                     get-melody-fn
                                                     get-strength-fn
                                                     get-structr-id]]
   [transit.util.random :refer [weighted-choice]]
   [transit.util.util :refer [remove-element-from-vector]]
   )
  )

;; method return values
(def OK 1)  ;; Method completed normally
(def CONTINUE 1)  ;; Processing complete - do not call additional methods
(def NEW-MELODY 2)  ;; Processing complete - last melody event is new
(def NEXT-METHOD 3)  ;; Select and call another method

(defn get-player-id
  [player]
  (:id player))

(defn get-player-structr
  [player structr-id]
  (first (for [structr (:structures player)
               :when (= structr-id (get-structr-id structr))]
           structr))
 )

(defn remove-structr
  [structr-vec structr-ndx]
  (remove-element-from-vector structr-vec structr-ndx)
  )

(defn remove-structr-by-structr-id
  [structr-vec structr-id]
  (remove-element-from-vector structr-vec
                              (first (keep-indexed
                                      #(if (= structr-id (get-structr-id %2))
                                         %1
                                         nil)
                                      structr-vec)))
  )

(defn create-random-rest-melody-event
  [player-id event-id]
  (create-melody-event :melody-event-id event-id
                       :note nil
                       :dur-info (select-random-rhythm)
                       :volume nil
                       :instrument-info nil
                       :player-id player-id
                       :event-time nil
                       :structr-id nil
                       )
  )

(defn create-nodur-rest-melody-event
  [player-id event-id]
  (create-melody-event :melody-event-id event-id
                       :note nil
                       :dur-info nil
                       :volume nil
                       :instrument-info nil
                       :player-id player-id
                       :event-time nil
                       :structr-id nil
                       )
  )

(defn select-random-pitch-for-player
  ([player]
   (select-random-pitch (:range-lo (:instrument-info player))
                        (:range-hi (:instrument-info player))
                        )
   )
  ([player lo hi]
   (select-random-pitch (:range-lo (:instrument-info player))
                        (:range-hi (:instrument-info player))
                        )
   )
  )

(defn cleanup-structr-id
  "Find structure for structr-id in player
   if found, call structr's cleanup-fn
             update player with new struct which was returned from cleanup-fn
             return updated player
   if not found, return player as it is
  "
  [player structr-id]
  (let [[structr-ndx  prev-structr] (first
                                     (keep-indexed
                                      #(if (= (get-structr-id %2) structr-id)
                                         [%1 %2]
                                         nil
                                         )
                                      (:structures player)
                                      ))
        cleanup-fn (if prev-structr (get-cleanup-fn prev-structr) nil)
        ]
    (if cleanup-fn
        (assoc player
               :structures
               (assoc (:structures player)
                      structr-ndx
                      (cleanup-fn prev-structr)))
      player
      ))
  )

(defn get-structr-strength
  [structr]
  ((get-strength-fn structr) structr))

(defn get-next-melody-event
  " Returns a melody-event and an updated player"
  [ensemble player melody player-id]
  (let [plyr-structs (:structures player)
        all-weights (mapv get-structr-strength plyr-structs)
        selected-struct-index (if (> (count all-weights) 0)
                                (weighted-choice all-weights)
                                nil)
        melody-struct (if selected-struct-index
                        (get plyr-structs selected-struct-index)
                        nil)
        [new-struct melody-event] (if melody-struct
                                    ((get-melody-fn melody-struct)
                                     ensemble
                                     player
                                     melody
                                     player-id
                                     melody-struct
                                     (inc (:melody-event-id (last melody)))
                                     )
                                    [nil nil]
                                    )
        prev-structr-id (get-structr-id-from-melody-event
                         (get melody (dec (count melody))))
        ;; if this melody-event has a different structr-id than
        ;; the prev melody-event, call cleanup-structr-id to update the player
        ;; by calling the previous structr-id cleanup-fn
        cleanup-player (if (or (not prev-structr-id)
                               (and melody-event
                                    (= (get-structr-id-from-melody-event
                                        melody-event)
                                       prev-structr-id))
                               )
                          player
                          (cleanup-structr-id player prev-structr-id))
        ]
    (cond (and melody-event new-struct)
          [(assoc cleanup-player
                  :structures
                  (assoc (:structures cleanup-player)
                         selected-struct-index
                         new-struct))
           melody-event
           ]

          ;; if there is a melody event, but new-struct is nil,
          ;; remove the structure from the player
          ;; melody-event
          ;; [(assoc cleanup-player
          ;;         :structures
          ;;         (remove-structr (:structures cleanup-player)
          ;;                         selected-struct-index
          ;;                         ))
          ;;  melody-event
          ;;  ]

          :else
          [cleanup-player
           (create-random-rest-melody-event
            player-id
            (inc (:melody-event-id (last melody))))
           ]
          )
    )
  )

(defn print-player
  "Pretty Print a player map

  player - the player map to print"
  [player & {:keys [prnt-full-inst-info]
             :or {prnt-full-inst-info false}}]
  (let [sorted-keys (sort (keys player))]
    (println "player: " (get-player-id player) "current time: " (System/currentTimeMillis))
    (doseq [player-key sorted-keys]
      (cond
        (= player-key :methods)
        (do
          (println " " player-key)
          (doseq [method-info (get player :methods)]
            (println "   " (type (:method method-info))
                     " weight: " (:weight method-info)
                     " created-at:: " (:created-at method-info)
                     )
            ))
        (and (= player-key :instrument-info) (= prnt-full-inst-info false))
        (do
          (println (format "%-29s" (str "  " player-key " :name")) "-" (:name (:instrument (:instrument-info player))))
          (println (format "%-29s" (str "  " player-key " :range-lo")) "-" (:range-lo (:instrument-info player)))
          (println (format "%-29s" (str "  " player-key " :range-hi")) "-" (:range-hi (:instrument-info player))))
        (= player-key :structures)
        (do
          (println " " player-key)
          (doseq [structr (:structures player)] (println "   " structr))
          )
        :else
        (println (format "%-20s" (str "  " player-key)) "-" (get player player-key)))
      )
    (println "end player: " (get-player-id player) "current time: " (System/currentTimeMillis))
    (prn)
    )
  )
