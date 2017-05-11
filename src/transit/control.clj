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

(ns transit.control
  (:require
   [transit.config.config :refer [get-setting set-setting]]
   [transit.util.print :refer [print-banner]]
   )
  )

(defn init-transit
  "Initialize transit to play. Use only once (first time)

   keyword args -
   :num-players - optional, the number of players playing.
                  default value is set in config file"
 [& {:keys [num-players]}]
  )

(defn start-transit
  []
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
