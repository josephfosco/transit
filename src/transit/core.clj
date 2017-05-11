;    Copyright (C) 2017 Joseph Fosco. All Rights Reserved
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

(ns transit.core
  (:gen-class)
  (:require
   [transit.control :refer [clear-transit pause-transit quit-transit
                            start-transit ]]
   [transit.version :refer [TRANSIT-VERSION-STR]]
   [overtone.live :as overtone]
   ))

 (defn -main
   [& args]
   (println "command line args:" args)
  )

(defn transit-start
  "Start playing.

   :num-players - optional key to set the number of players
                  default value is set in config file"
  [& {:keys [num-players]}]
  (start-transit :num-players num-players)
)

(defn transit-quit
  "Quit Transit and exit Clojure"
  []
  (quit-transit)
  )

(defn transit-exit
  "same as transit-quit"
  []
  (quit-transit))

(defn transit-pause
  "Stop playing after players finish what they have scheduled"
  []
  (pause-transit)
)

(defn transit-clear
  "Clears the scheduler, message-processor, and players"
  []
  (clear-transit)
  )

(defn transit-help
  []
  (println)
  (println "TRANSIT version" TRANSIT-VERSION-STR)
  (print
   "
   Functions to run transit

     (transit-start)        Start playing
                               optional key :num-players
     (transit-pause)        Pause after playing current notes

     (transit-help)         Print this message


"))

(defn start64
  []
  (transit-start :num-players 64))

(defn stop
  []
  (overtone/stop))

(transit-help)
