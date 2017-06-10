package library;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * A module that sets up the library.
 */
public class LibraryModule extends AbstractModule{

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(Dict.class, DictImpl.class)//
				.build(DictFactory.class));
		install(new FactoryModuleBuilder().implement(DoubleKeyDict.class, DoubleKeyDictImpl.class).build(DoubleKeyDictFactory.class));
	}

}
