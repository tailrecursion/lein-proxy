(ns leiningen.proxy
  (:refer-clojure :exclude [proxy])
  (:require [clojure.tools.cli        :refer [cli]]
            [ring.adapter.jetty       :refer [run-jetty]]
            [compojure.route          :refer [files]]
            [tailrecursion.ring-proxy :refer [wrap-proxy]])) 

(defn wrap-dir-index [handler]
  (fn [req]
    (handler
     (update-in req [:uri]
                #(if-let [dir (re-matches #"^.*/" %)]
                   (str dir "index.html")
                   %)))))


(defn proxy
  "Proxy and static file server.
  
  USAGE: lein proxy [OPTIONS]
  
  OPTIONS:
    -p <port>, --port <port>
        Listen on this port (9090).

    -s <path>, --static <path>
        Serve static files from this path. (Default is the default
        resource path used by compojure.)

    -P <prefix>, --prefix <prefix>
        Proxy all requests whose path begins with <prefix> ('').

    -r <uri>, --remote <uri>
        The remote uri which will service proxied requests. There is no
        default; this parameter is required.

  PROJECT.CLJ:
    You may specify an options map in the project.clj file under the
    :proxy key. The map may contain any of the following keys: :port,
    :static, :prefix, and/or :remote.

  EXAMPLE:
    If the <prefix> is '/foo' and the <remote> is 'http://bar.com/baz',
    and a request is made for '/foo/baf/quux.gif', the request will be
    proxied to the URI 'http://bar.com/baz/baf/quux.gif'."
  [project & args]
  (let [p     (:proxy project)
        opts  [["-p" "--port" "Listen on this port"
                :default (or (:port p) 9090) :parse-fn #(Integer. %)]
               ["-s" "--static" "Location of static files."
                :default (:static p)]
               ["-P" "--prefix" "Paths that start with this will be proxied."
                :default (or (:prefix p) "")]
               ["-r" "--remote" "Remote URI which will service proxy requests."
                :default (:remote p)]]
        [cfg]  (apply cli args opts)]
    (if (not (and (:prefix cfg) (:remote cfg)))
      (throw (Exception. "Please specify both prefix and remote.")))
    (prn cfg)
    (run-jetty (-> (if (:static cfg)
                     (files "/" {:root (:static cfg)})
                     (files "/")) 
                   (wrap-proxy (:prefix cfg) (:remote cfg))
                   wrap-dir-index)
               {:port (:port cfg)})))
