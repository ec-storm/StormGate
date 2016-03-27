/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Channel;

import java.util.List;

public interface ProfileService {

    List<Profile> findAllProfile();

    List<Channel> findAllChannelByProfile(Profile profile);

    boolean profileExists(String profileName);

    Channel save(Channel channel);

    Profile save(Profile profile);
}
