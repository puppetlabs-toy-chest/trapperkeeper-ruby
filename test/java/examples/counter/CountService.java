package examples.counter;

/**
 * This class basically provides a bridge that allows Java (or JRuby) code
 * to call the function provided by the clojure
 * `examples.counter.clj-count-service/count-service`.
 */
public abstract class CountService {

    // This abstract method is the main interface.  We deal with providing
    // an implementation of it from within the clojure code.  A more interesting
    // service would probably have several such method signatures, rather than
    // just one.
    public abstract int incAndGet();

    // This variable is here to hold a reference to a singleton instance of
    // the service.
    private static CountService instance;

    // This getter allows access to the singleton instance
    public static CountService getInstance() {
        return instance;
    }

    // This setter is only here to allow the clojure code to register the
    // singleton.  It should probably have a bit more error checking to make
    // sure it's only called once, or something :)
    public static void setInstance(CountService cs) {
        instance = cs;
    }

}
