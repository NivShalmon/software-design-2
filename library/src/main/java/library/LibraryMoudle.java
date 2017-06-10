package library;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class LibraryMoudle extends AbstractModule{

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(Dict.class, DictImpl.class)//
				.build(DictFactory.class));
		install(new FactoryModuleBuilder().implement(DoubleKeyDict.class, DoubleKeyDictImpl.class).build(DoubleKeyDictFactory.class));
	}

}
