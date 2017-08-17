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

(ns transit.melody.melody-event)

(defrecord MelodyEvent [id note dur-info volume instrument-info player-id event-time play-time sc-instrument-id note-off])

;; MelodyEvent fields
;;  id   - id sequence of melody event - 0 is initial blank event
;;  note - note number of event - nil for rest
;;  event-time - time (in millis) event was supposed to be played
;;  play-time  - time (in millis) event was actually played
;;  note-off - true if a note-off was scheduled for this event note
;;             false if note-off event was not scheduled for this note
;;             nil if this event is a rest (note = nil)

(defn create-melody-event
  [& {:keys [:id :note :dur-info :volume :instrument-info :player-id :event-time :play-time :sc-instrument-id :note-off]}]
  (MelodyEvent. id
                note
                dur-info
                volume
                instrument-info
                player-id
                event-time
                nil  ;; :play-time
                nil  ;; sc-instrument-id
                note-off
                )
  )

(defn set-sc-instrument-id-and-times
  [melody-event sc-instrument-id event-time play-time]
  (assoc melody-event
         :event-time event-time
         :play-time play-time
         :sc-instrument-id sc-instrument-id
         ))

(defn get-dur-info-from-melody-event
 [melody-event]
 (:dur-info melody-event)
 )

(defn get-instrument-info-from-melody-event
 [melody-event]
 (:instrument-info melody-event)
 )

(defn get-note-from-melody-event
 [melody-event]
 (:note melody-event)
 )

(defn get-note-off-from-melody-event
 [melody-event]
 (:note-off melody-event)
 )

(defn get-sc-instrument-id-from-melody-event
 [melody-event]
 (:sc-instrument-id melody-event)
 )

(defn get-volume-from-melody-event
 [melody-event]
 (:volume melody-event)
 )

(defn print-melody-event
  [melody-event]
  (let [inst-inf (:instrument-info melody-event)
        melody-map (into (sorted-map)
                         (assoc
                          melody-event
                          :instrument-info
                          {:name (:name (:instrument inst-inf))
                           :range-lo (:range-lo inst-inf)
                           :range-hi (:range-hi inst-inf)}
                          :dur-info
                          (into {} (:dur-info melody-event))
                          ))
        ]
    (println melody-map)
    )
  )
