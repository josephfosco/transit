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

(ns transit.player.player-play-note
  (:require
   [overtone.live :refer [midi->hz]]
   [transit.instr.instrumentinfo :refer [get-release-millis-from-instrument-info
                                         get-instrument-from-instrument-info]]
   [transit.instr.sc-instrument :refer [stop-instrument]]
   [transit.config.config :refer [get-setting]]
   [transit.ensemble.ensemble :refer [get-melody get-player
                                      update-player-and-melody]]
   [transit.melody.dur-info :refer [get-dur-millis-from-dur-info]]
   [transit.melody.melody-event :refer [get-dur-info-from-melody-event
                                        get-instrument-info-from-melody-event
                                        get-note-from-melody-event
                                        get-note-off-from-melody-event
                                        get-sc-instrument-id-from-melody-event
                                        get-volume-from-melody-event
                                        set-sc-instrument-id-and-times]]
   [transit.player.player-methods :refer [NEW-MELODY NEXT-METHOD]]
   [transit.util.random :refer [weighted-choice]]
   [transit.util.util :refer [remove-element-from-vector]]
   )
  )

(defn get-player-method
  [player ndx]
  (first ((:methods player) ndx))
  )

(defn is-playing?
 "Returns:
   true - if player is playing now
   false - if player is not playing now
 "
 [player]
  )

(defn sched-note-off?
  "If this is not a rest and
    this note has a release
    the note length > release
  then return true
  else return false"
  [melody-event]

  (if (and (not (nil? (get-note-from-melody-event melody-event)))
           (> (get-release-millis-from-instrument-info
               (get-instrument-info-from-melody-event melody-event))
              (get-dur-millis-from-dur-info
               (get-dur-info-from-melody-event melody-event)))
           )
    true
    false)
  )

(defn select-method
  " Returns the ndx into player-methods of the method to run "
  [player]
  (weighted-choice (mapv second (:methods player)))
  )

(defn run-player-method
  " Selects and executes one of the player :methods
    Returns player after executing method with the selected
      method removed from :methods
  "
  [[ensemble player melody player-id :as method_context]]
  (let [method-ndx (select-method player)]
    ;; remove the method that will be run from player :methods
    ((get-player-method player method-ndx)
     [ensemble
      (assoc player
             :methods (remove-element-from-vector (:methods player)  method-ndx))
      melody
      player-id
      ])
     )
  )

(defn check-prior-event-note-off
   " if the prior note was not turned off and
       either this note is a rest or
         this note has a different instrument than the prior note
     then
       turn off the prior note"
  [prior-melody-event cur-melody-event]
  (when (and (false? (get-note-off-from-melody-event prior-melody-event))
             (or (not (nil? (get-note-from-melody-event cur-melody-event)))
                 (not=
                  (get-sc-instrument-id-from-melody-event prior-melody-event)
                  (get-sc-instrument-id-from-melody-event cur-melody-event)
                  )
                 )
             )
    (stop-instrument (get-sc-instrument-id-from-melody-event prior-melody-event))
    )
  )

(defn stop-running-methods?
  [event_time [_ player melody player-id rtn-map]]
  (or (= 0 (count (:methods player )))
      (not= (:status rtn-map) NEXT-METHOD)
      )
  )

(defn play-melody-event
  [melody-event event-time]
  (println "*------------* play-melody-event *------------*")
  (println melody-event)
  (let [note (get-note-from-melody-event melody-event)
        ;; the following line plays the note
        cur-inst-id ((get-instrument-from-instrument-info
                      (get-instrument-info-from-melody-event melody-event))
                     (midi->hz (get-note-from-melody-event melody-event))
                     (* (get-volume-from-melody-event melody-event)
                        (get-setting :volume-adjust))
                     )
        full-melody-event (set-sc-instrument-id-and-times melody-event
                                                          cur-inst-id
                                                          event-time
                                                          (System/currentTimeMillis))
        note-off?? (sched-note-off? full-melody-event)

          ]
       full-melody-event
    )
  )

(defn play-next-note
  [ensemble player-id event-time]
  (let [player (get-player ensemble player-id)
        melody (get-melody ensemble player-id)
        method-context [ensemble player melody player-id {:status NEXT-METHOD}]
        [_ new-player new-melody player-id rtn-map]
        (first (filter (partial stop-running-methods? event-time)
                       (iterate run-player-method method-context)))
        upd-melody (if (= (:status rtn-map) NEW-MELODY)
                     (assoc new-melody
                            (dec (count new-melody))
                            (play-melody-event (last new-melody) event-time))
                     new-melody)
        ]
    (check-prior-event-note-off (last melody) upd-melody)
    (update-player-and-melody new-player upd-melody player-id)
    )
  (println (- (System/currentTimeMillis) event-time))
 )
