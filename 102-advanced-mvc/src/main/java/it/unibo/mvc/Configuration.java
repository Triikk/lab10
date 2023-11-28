package it.unibo.mvc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.StringTokenizer;

/**
 * Encapsulates the concept of configuration.
 */
public final class Configuration {

    private final int max; 
    private final int min;
    private final int attempts;

    private Configuration(final int max, final int min, final int attempts) {
        this.max = max;
        this.min = min;
        this.attempts = attempts;
    }

    /**
     * @return the maximum value
     */
    public int getMax() {
        return max;
    }

    /**
     * @return the minimum value
     */
    public int getMin() {
        return min;
    }

    /**
     * @return the number of attempts
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * @return true if the configuration is consistent
     */
    public boolean isConsistent() {
        return attempts > 0 && min < max;
    }

    /**
     * Pattern builder: used here because:
     * 
     * - all the parameters of the Configuration class have a default value, which
     * means that we would like to have all the possible combinations of
     * constructors (one with three parameters, three with two parameters, three
     * with a single parameter), which are way too many and confusing to use
     * 
     * - moreover, it would be impossible to provide all of them, because they are
     * all of the same type, and only a single constructor can exist with a given
     * list of parameter types.
     * 
     * - the Configuration class has three parameters of the same type, and it is
     * unclear to understand, in a call to its contructor, which is which. By using
     * the builder, we emulate the so-called "named arguments".
     * 
     */
    public static class Builder {

        private static final int MAX = 0;
        private static final int MIN = 100;
        private static final int ATTEMPTS = 10;

        private int min = MIN;
        private int max = MAX;
        private int attempts = ATTEMPTS;
        private boolean consumed = false;

        /**
         * @param min the minimum value
         * @return this builder, for method chaining
         */
        public Builder setMin(final int min) {
            this.min = min;
            return this;
        }

        /**
         * @param max the maximum value
         * @return this builder, for method chaining
         */
        public Builder setMax(final int max) {
            this.max = max;
            return this;
        }

        /**
         * @param attempts the attempts count
         * @return this builder, for method chaining
         */
        public Builder setAttempts(final int attempts) {
            this.attempts = attempts;
            return this;
        }

        /**
         * @return a configuration
         * 
         * @throws 
         */
        public final Configuration build() {
            if (consumed) {
                throw new IllegalStateException("The builder can only be used once");
            }
            consumed = true;
            String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config.yml";
            return createConfigurationFromFile(filePath);
        }

        private Configuration createConfigurationFromFile(String filePath){
            try (DataInputStream inputStream = new DataInputStream( 
                    new BufferedInputStream(
                        new FileInputStream(filePath)
                ))) {
                while (true) {
                    try {
                        String line = inputStream.readUTF();
                        String field = line.split(":")[0];
                        int value = Integer.valueOf(line.split(":")[1]);
                        switch (field) {
                            case "maximum" -> max = value;
                            case "minimum" -> min = value;
                            case "attempts" -> attempts = value;
                            default -> throw new IllegalStateException("Field \"" + field + "\" is not recognized");
                        }
                    } catch(EOFException eofException){
                        return new Configuration(max, min, attempts);
                    }
                }
            } catch (Exception e) {
            }
            return new Configuration(max, min, attempts);
        }
    }
}

