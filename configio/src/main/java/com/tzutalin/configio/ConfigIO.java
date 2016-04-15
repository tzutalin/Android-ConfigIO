package com.tzutalin.configio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Created by darrenl on 2016/4/15.
 */

/**
 *
 * A class to read and write configuration to the specified path like external storage
 *
 */
public class ConfigIO {
    private Context mContext;
    private Writer mWriter;
    private String mTargetPath;

    private ConfigIO() {

    }

    public ConfigIO(Context context) {
        mContext = context;
    }
    
    @NonNull
    public void init(@NonNull String path) {
        mTargetPath = path;
    }

    public Writer getWriter() {
        return mWriter;
    }

    public interface Writer {
        /**
         * Set a String value in the configuration Writer, to be written back once
         * {@link #commit} or {@link #apply} are called.
         *
         * @param key   The name of the configuration to modify.
         * @param value The new value for the configuration.
         * @return Returns a reference to the same Writer object, so you can
         * chain put calls together.
         */
        Writer putString(String key, @Nullable String value);

        /**
         * Set a set of String values in the configuration Writer, to be written
         * back once {@link #commit} or {@link #apply} is called.
         *
         * @param key    The name of the configuration to modify.
         * @param values The set of new values for the configuration.  Passing {@code null}
         *               for this argument is equivalent to calling {@link #remove(String)} with
         *               this key.
         * @return Returns a reference to the same Writer object, so you can
         * chain put calls together.
         */
        Writer putStringSet(String key, @Nullable Set<String> values);

        /**
         * Set an int value in the configuration Writer, to be written back once
         * {@link #commit} or {@link #apply} are called.
         *
         * @param key   The name of the configuration to modify.
         * @param value The new value for the configuration.
         * @return Returns a reference to the same Writer object, so you can
         * chain put calls together.
         */
        Writer putInt(String key, int value);

        /**
         * Set a long value in the configuration Writer, to be written back once
         * {@link #commit} or {@link #apply} are called.
         *
         * @param key   The name of the configuration to modify.
         * @param value The new value for the configuration.
         * @return Returns a reference to the same Writer object, so you can
         * chain put calls together.
         */
        Writer putLong(String key, long value);

        /**
         * Set a float value in the configuration Writer, to be written back once
         * {@link #commit} or {@link #apply} are called.
         *
         * @param key   The name of the configuration to modify.
         * @param value The new value for the configuration.
         * @return Returns a reference to the same Writer object, so you can
         * chain put calls together.
         */
        Writer putFloat(String key, float value);

        /**
         * Set a boolean value in the configuration Writer, to be written back
         * once {@link #commit} or {@link #apply} are called.
         *
         * @param key   The name of the configuration to modify.
         * @param value The new value for the configuration.
         * @return Returns a reference to the same Writer object, so you can
         * chain put calls together.
         */
        Writer putBoolean(String key, boolean value);

        /**
         * Mark in the Writer that a configuration value should be removed, which
         * will be done in the actual configuration once {@link #commit} is
         * called.
         * <p/>
         * <p>Note that when committing back to the configuration, all removals
         * are done first, regardless of whether you called remove before
         * or after put methods on this Writer.
         *
         * @param key The name of the configuration to remove.
         * @return Returns a reference to the same Writer object, so you can
         * chain put calls together.
         */
        Writer remove(String key);

        /**
         * Mark in the Writer to remove <em>all</em> values from the
         * configuration.  Once commit is called, the only remaining configuration
         * will be any that you have defined in this Writer.
         * <p/>
         * <p>Note that when committing back to the configuration, the clear
         * is done first, regardless of whether you called clear before
         * or after put methods on this Writer.
         *
         * @return Returns a reference to the same Writer object, so you can
         * chain put calls together.
         */
        Writer clear();

        /**
         * Commit your configuration changes back from this Writer to the
         * {@link ConfigIO} object it is editing.  This atomically
         * performs the requested modifications, replacing whatever is currently
         * in the ConfigIO.
         * <p/>
         * <p>Note that when two Writers are modifying configuration at the same
         * time, the last one to call commit wins.
         * <p/>
         * <p>If you don't care about the return value and you're
         * using this from your application's main thread, consider
         * using {@link #apply} instead.
         *
         * @return Returns true if the new values were successfully written
         * to persistent storage.
         */
        boolean commit();

        /**
         * Commit your configuration changes back from this Writer to the
         * {@link ConfigIO} object it is editing.  This atomically
         * performs the requested modifications, replacing whatever is currently
         * in the ConfigIO.
         * <p/>
         * <p>Note that when two Writers are modifying configuration at the same
         * time, the last one to call apply wins.
         * <p/>
         * <p>Unlike {@link #commit}, which writes its configuration out
         * to persistent storage synchronously, {@link #apply}
         * commits its changes to the in-memory
         * {@link ConfigIO} immediately but starts an
         * asynchronous commit to disk and you won't be notified of
         * any failures.  If another Writer on this
         * {@link ConfigIO} does a regular {@link #commit}
         * while a {@link #apply} is still outstanding, the
         * {@link #commit} will block until all async commits are
         * completed as well as the commit itself.
         * <p/>
         * <p>As {@link ConfigIO} instances are singletons within
         * a process, it's safe to replace any instance of {@link #commit} with
         * {@link #apply} if you were already ignoring the return value.
         * <p/>
         * <p>You don't need to worry about Android component
         * lifecycles and their interaction with <code>apply()</code>
         * writing to disk.  The framework makes sure in-flight disk
         * writes from <code>apply()</code> complete before switching
         * states.
         * <p/>
         * <p class='note'>The ConfigIO.Writer interface
         * isn't expected to be implemented directly.  However, if you
         * previously did implement it and are now getting errors
         * about missing <code>apply()</code>, you can simply call
         * {@link #commit} from <code>apply()</code>.
         */
        void apply();

    }

    /**
     * Retrieve all values from the Config File.
     * <p/>
     * <p>Note that you <em>must not</em> modify the collection returned
     * by this method, or alter any of its contents.  The consistency of your
     * stored data is not guaranteed if you do.
     *
     * @return Returns a map containing a list of pairs key/value representing
     * the Config File.
     * @throws NullPointerException
     */
    public Map<String, ?> getAll() {
        return null;
    }

    /**
     * Retrieve a String value from the Config File.
     *
     * @param key      The name of the map to retrieve.
     * @param defValue Value to return if this map does not exist.
     * @return Returns the map value if it exists, or defValue.  Throws
     * ClassCastException if there is a map with this name that is not
     * a String.
     * @throws ClassCastException
     */
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        return null;
    }

    /**
     * Retrieve a set of String values from the Config File.
     * <p/>
     * <p>Note that you <em>must not</em> modify the set instance returned
     * by this call.  The consistency of the stored data is not guaranteed
     * if you do, nor is your ability to modify the instance at all.
     *
     * @param key       The name of the map to retrieve.
     * @param defValues Values to return if this map does not exist.
     * @return Returns the map values if they exist, or defValues.
     * Throws ClassCastException if there is a map with this name
     * that is not a Set.
     * @throws ClassCastException
     */
    @Nullable
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return null;
    }

    /**
     * Retrieve an int value from the Config File.
     *
     * @param key      The name of the map to retrieve.
     * @param defValue Value to return if this map does not exist.
     * @return Returns the map value if it exists, or defValue.  Throws
     * ClassCastException if there is a map with this name that is not
     * an int.
     * @throws ClassCastException
     */
    public int getInt(String key, int defValue) {
        return 1;
    }

    /**
     * Retrieve a long value from the Config File.
     *
     * @param key      The name of the map to retrieve.
     * @param defValue Value to return if this map does not exist.
     * @return Returns the map value if it exists, or defValue.  Throws
     * ClassCastException if there is a map with this name that is not
     * a long.
     * @throws ClassCastException
     */
    public long getLong(String key, long defValue) {
        return 1;
    }

    /**
     * Retrieve a float value from the Config File.
     *
     * @param key      The name of the map to retrieve.
     * @param defValue Value to return if this map does not exist.
     * @return Returns the map value if it exists, or defValue.  Throws
     * ClassCastException if there is a map with this name that is not
     * a float.
     * @throws ClassCastException
     */
    public float getFloat(String key, float defValue) {
        return 1;
    }

    /**
     * Retrieve a boolean value from the Config File.
     *
     * @param key      The name of the map to retrieve.
     * @param defValue Value to return if this map does not exist.
     * @return Returns the map value if it exists, or defValue.  Throws
     * ClassCastException if there is a map with this name that is not
     * a boolean.
     * @throws ClassCastException
     */
    public boolean getBoolean(String key, boolean defValue) {
        return true;
    }

    /**
     * Checks whether the Config File contains a key.
     *
     * @param key The name of the map to check.
     * @return Returns true if the map exists in the Config File,
     * otherwise false.
     */
    public boolean contains(String key) {
        return true;
    }

}
