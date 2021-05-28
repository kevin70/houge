/*
 * Copyright 2019-2021 the original author or authors
 *
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
 */
package cool.houge.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import cool.houge.service.group.GroupServiceImpl;

/**
 * {@link GroupServiceImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class GroupServiceImplTest {

//  @Test
//  void createGroup() {
//    var groupDao = mock(GroupDao.class);
//    var groupQueryDao = mock(GroupQueryDao.class);
//    var groupService = new GroupServiceImpl(groupDao, groupQueryDao);
//
//    var bean = Create.builder().gid(1L).build();
//    when(groupDao.insert(any())).thenReturn(Mono.just(bean.getGid()));
//
//    var p = groupService.create(bean);
//    StepVerifier.create(p)
//        .consumeNextWith(
//            result -> assertThat(result).hasFieldOrPropertyWithValue("id", bean.getGid()))
//        .expectComplete()
//        .verify();
//
//    verify(groupDao).insert(any());
//  }
//
//  @Test
//  void deleteGroup() {
//    var groupDao = mock(GroupDao.class);
//    var groupQueryDao = mock(GroupQueryDao.class);
//    var groupService = new GroupServiceImpl(groupDao, groupQueryDao);
//
//    when(groupDao.delete(anyLong())).thenReturn(Mono.empty());
//
//    var p = groupService.delete(1L);
//    StepVerifier.create(p).expectComplete().verify();
//
//    verify(groupDao).delete(anyLong());
//  }
//
//  @Test
//  void existsById() {
//    var groupDao = mock(GroupDao.class);
//    var groupQueryDao = mock(GroupQueryDao.class);
//    var groupService = new GroupServiceImpl(groupDao, groupQueryDao);
//
//    Roaring64NavigableMap existingGidBits =
//        Whitebox.getInternalState(groupService, "existingGidBits");
//    var gid = 1L;
//    existingGidBits.add(gid);
//
//    StepVerifier.create(groupService.existsById(gid))
//        .expectNext(Nil.INSTANCE)
//        .expectComplete()
//        .verify();
//  }
//
//  @Test
//  void existsById2() {
//    var groupDao = mock(GroupDao.class);
//    var groupQueryDao = mock(GroupQueryDao.class);
//    var groupService = new GroupServiceImpl(groupDao, groupQueryDao);
//
//    var gid = 1L;
//    when(groupQueryDao.existsById(gid)).thenReturn(Nil.mono());
//    StepVerifier.create(groupService.existsById(gid))
//        .expectNext(Nil.INSTANCE)
//        .expectComplete()
//        .verify();
//    verify(groupQueryDao).existsById(gid);
//
//    var gid2 = 2l;
//    when(groupQueryDao.existsById(gid2)).thenReturn(Mono.empty());
//    StepVerifier.create(groupService.existsById(gid2)).expectComplete().verify();
//    verify(groupQueryDao).existsById(gid2);
//  }
//
//  @Test
//  void joinMember() {
//    var groupDao = mock(GroupDao.class);
//    var groupQueryDao = mock(GroupQueryDao.class);
//    var groupService = new GroupServiceImpl(groupDao, groupQueryDao);
//
//    var gid = 1L;
//    var uid = 1L;
//    var vo = new JoinGroupVO();
//    vo.setUid(uid);
//
//    when(groupQueryDao.existsById(gid)).thenReturn(Nil.mono());
//    when(groupDao.joinMember(gid, uid)).thenReturn(Mono.empty());
//    //    StepVerifier.create(groupService.joinMember(vo)).expectComplete().verify();
//    verify(groupQueryDao).existsById(gid);
//    verify(groupDao).joinMember(gid, uid);
//  }
//
//  @Test
//  void joinMember_NotFoundGroup() {
//    var groupDao = mock(GroupDao.class);
//    var groupQueryDao = mock(GroupQueryDao.class);
//    var groupService = new GroupServiceImpl(groupDao, groupQueryDao);
//
//    var gid = 1L;
//    var uid = 1L;
//    var vo = new JoinGroupVO();
//    vo.setUid(uid);
//
//    when(groupQueryDao.existsById(gid)).thenReturn(Mono.empty());
//    //    var p = groupService.joinMember(vo);
//    //
//    //    StepVerifier.create(p)
//    //        .consumeErrorWith(
//    //            ex ->
//    //                assertThat(ex)
//    //                    .isInstanceOf(StacklessBizCodeException.class)
//    //                    .hasFieldOrPropertyWithValue("bizCode", BizCode.C404))
//    //        .verify();
//    //    verify(groupQueryDao).existsById(gid);
//  }
//
//  @Test
//  void deleteMember() {
//    var groupDao = mock(GroupDao.class);
//    var groupQueryDao = mock(GroupQueryDao.class);
//    var groupService = new GroupServiceImpl(groupDao, groupQueryDao);
//
//    var gid = 1L;
//    var uid = 1L;
//    var vo = new JoinGroupVO();
//    vo.setUid(uid);
//
//    when(groupQueryDao.existsById(gid)).thenReturn(Nil.mono());
//    when(groupDao.removeMember(gid, uid)).thenReturn(Mono.empty());
//
//    //    StepVerifier.create(groupService.deleteMember(vo)).expectComplete().verify();
//    //    verify(groupQueryDao).existsById(gid);
//    //    verify(groupDao).removeMember(gid, uid);
//  }
//
//  @Test
//  void deleteMember_NotFoundGroup() {
//    var groupDao = mock(GroupDao.class);
//    var groupQueryDao = mock(GroupQueryDao.class);
//    var groupService = new GroupServiceImpl(groupDao, groupQueryDao);
//
//    var gid = 1L;
//    var uid = 1L;
//    var vo = new JoinGroupVO();
//    vo.setUid(uid);
//
//    when(groupQueryDao.existsById(gid)).thenReturn(Mono.empty());
//    //    var p = groupService.deleteMember(vo);
//    //
//    //    StepVerifier.create(p)
//    //        .consumeErrorWith(
//    //            ex ->
//    //                assertThat(ex)
//    //                    .isInstanceOf(StacklessBizCodeException.class)
//    //                    .hasFieldOrPropertyWithValue("bizCode", BizCode.C404))
//    //        .verify();
//    verify(groupQueryDao).existsById(gid);
//  }
}
