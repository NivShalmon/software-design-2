package library;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;
import library.TestStorerFactory;

public class TestLineStorageModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FutureLineStorageFactory.class).to(TestStorerFactory.class);
		bind(FutureLineStorage.class).to(TestStorer.class);
		install(new FactoryModuleBuilder().implement(Dict.class, DictImpl.class)//
				.build(DictImplFactory.class));
		install(new FactoryModuleBuilder().implement(DoubleKeyDict.class, DoubleKeyDictImpl.class).build(DoubleKeyDictImplFactory.class));
	}

}
