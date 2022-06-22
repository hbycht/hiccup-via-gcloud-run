/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudrun.microservicetemplate;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** Example REST controller to demonstrate structured logging. */
@RestController
public class MicroserviceController {

  /** Example endpoint handler. */
  @GetMapping("/")
  public @ResponseBody String index() {
    return "Hello Worldzzzz!";
  }

  /** Another example endpoint handler. */
  @GetMapping("/cool")
  public @ResponseBody String cool() {
    return "Cooooool! Another endpoint!";
  }
}
