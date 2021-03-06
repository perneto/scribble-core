/*
 * Copyright 2009-10 www.scribble.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.protocol.validation.rules;

import java.text.MessageFormat;

import org.scribble.protocol.model.*;

public class RepeatValidatorRuleTest {

	@org.junit.Test
	public void testRepeatUnknownRole() {
		
		Protocol prot1=new Protocol();
		
		RoleList plist1=new RoleList();
		prot1.getBlock().add(plist1);
		
		Role part1=new Role();
		part1.setName("part1");
		plist1.getRoles().add(part1);
		
		Role part2=new Role();
		part2.setName("part2");
		plist1.getRoles().add(part2);
		
		Repeat r1=new Repeat();
		prot1.getBlock().add(r1);
		
		Role part3=new Role();
		part3.setName("part3");
		
		r1.getRoles().add(part3);
				
		TestScribbleLogger logger=new TestScribbleLogger();

		RepeatValidatorRule rule=new RepeatValidatorRule();
		rule.validate(r1, logger);
		
		logger.verifyErrors(new String[]{
				MessageFormat.format(
						java.util.PropertyResourceBundle.getBundle(
						"org.scribble.protocol.Messages").getString("_UNKNOWN_ROLE"),
							part3.getName())
		});
	}

	@org.junit.Test
	public void testRepeatValidRole() {
		
		Protocol prot1=new Protocol();
		
		RoleList plist1=new RoleList();
		prot1.getBlock().add(plist1);
		
		Role part1=new Role();
		part1.setName("part1");
		plist1.getRoles().add(part1);
		
		Role part2=new Role();
		part2.setName("part2");
		plist1.getRoles().add(part2);
		
		Repeat r1=new Repeat();
		prot1.getBlock().add(r1);
		
		Role part3=new Role();
		part3.setName("part2");
		
		r1.getRoles().add(part3);
				
		TestScribbleLogger logger=new TestScribbleLogger();

		RepeatValidatorRule rule=new RepeatValidatorRule();
		rule.validate(r1, logger);
		
		logger.verifyErrors(new String[]{
		});
	}
}
