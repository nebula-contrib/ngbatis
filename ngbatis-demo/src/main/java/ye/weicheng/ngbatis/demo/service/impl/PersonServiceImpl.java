// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ye.weicheng.ngbatis.demo.repository.TestRepository;
import ye.weicheng.ngbatis.demo.service.PersonService;

/**
 * @author yeweicheng
 * @since 2022-06-17 7:18 <br>
 *        Now is history!
 */
@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private TestRepository repository;
}
