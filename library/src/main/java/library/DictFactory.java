package library;

/**
 * A factory for creating {@link DictImpl} using assisted injection. See
 * {@link https://github.com/google/guice/wiki/AssistedInject} for more info
 */
public interface DictFactory {

	public Dict create(String name);
}