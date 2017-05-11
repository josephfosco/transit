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

(ns transit.config.config
  (:require
   [clojure.java.io :as io]
   )
  )

(defn load-config
  [filename]
  (with-open [r (io/reader filename)]
    (binding [*read-eval* false]
             (read (java.io.PushbackReader. r))))
  )

(declare settings)
(defn get-setting
  [key]
  (key @settings)
  )

(defn set-setting
  [key val]
  (swap! settings assoc key val)
  )

(def settings (atom (load-config "src/transit/config/init_settings.clj")))
