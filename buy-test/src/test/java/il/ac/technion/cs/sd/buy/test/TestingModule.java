package il.ac.technion.cs.sd.buy.test;

import com.google.inject.AbstractModule;

import library.Dict;
import library.DoubleKeyDict;

public class TestingModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Dict.class).to(DictTestImpl.class);
		bind(DoubleKeyDict.class).to(DoubleDictTestImpl.class);
	}

}
