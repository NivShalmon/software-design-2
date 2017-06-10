package il.ac.technion.cs.sd.buy.test;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import library.Dict;
import library.DoubleKeyDict;

public class TestingModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Dict.class).annotatedWith(Names.named("orderIdToOrder")).to(DictTestImpl.class);
		bind(Dict.class).annotatedWith(Names.named("userIdToOrderIds")).to(DictTestImpl.class);
		bind(Dict.class).annotatedWith(Names.named("productIdToOrderIds")).to(DictTestImpl.class);
		bind(Dict.class).annotatedWith(Names.named("orderIdToHistory")).to(DictTestImpl.class);
		bind(DoubleKeyDict.class).annotatedWith(Names.named("userProductAmount")).to(DoubleDictTestImpl.class);
	}

}
