/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */

package org.jnosql.diana.redis.key;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class DefaultSortedSetTest {

    private static final String BRAZIL = "Brazil";
    private static final String USA = "USA";
    private static final String ENGLAND = "England";

    private RedisKeyValueEntityManagerFactory keyValueEntityManagerFactory;
    private SortedSet sortedSet;

    @Before
    public void init() {
        keyValueEntityManagerFactory = RedisTestUtils.get();
        sortedSet = keyValueEntityManagerFactory.getSortedSet("world-cup-2018");
        sortedSet.remove(BRAZIL);
        sortedSet.remove(USA);
        sortedSet.remove(ENGLAND);
    }

    @Test
    public void shouldAdd() {
        sortedSet.add(BRAZIL, 10);
    }

    @Test
    public void shouldSize() {
        assertTrue(sortedSet.isEmpty());
        sortedSet.add(BRAZIL, 10);
        assertEquals(Integer.valueOf(sortedSet.size()), Integer.valueOf(1));
    }

    @Test
    public void shouldCheckIfEmpty() {

        assertTrue(sortedSet.isEmpty());
        sortedSet.add(BRAZIL, 1);
        sortedSet.add(USA, 2);
        sortedSet.add(ENGLAND, 3);
        assertFalse(sortedSet.isEmpty());

    }

    @Test
    public void souldDelete() {
        sortedSet.add(BRAZIL, 1);
        sortedSet.add(USA, 2);
        sortedSet.add(ENGLAND, 3);
        assertFalse(sortedSet.isEmpty());
        sortedSet.delete();
        assertTrue(sortedSet.isEmpty());
    }

    @Test
    public void shouldIncrement() {
        sortedSet.add(BRAZIL, 10);
        Number points = sortedSet.increment(BRAZIL, 2);
        assertEquals(Long.valueOf(12), Long.valueOf(points.longValue()));
    }

    @Test
    public void shouldDecrement() {
        sortedSet.add(BRAZIL, 10);
        Number points = sortedSet.decrement(BRAZIL, 2);
        assertEquals(Long.valueOf(8), Long.valueOf(points.longValue()));
    }

    @Test
    public void shouldRemoveMember() {
        sortedSet.add(BRAZIL, 10);
        sortedSet.remove(BRAZIL);
        assertTrue(sortedSet.size() == 0);
    }

    @Test
    public void shouldShouldExpires() throws InterruptedException {
        sortedSet.add(BRAZIL, 10);
        sortedSet.expire(Duration.ofSeconds(1));
        Thread.sleep(2_000L);
        assertTrue(sortedSet.size() == 0);
    }

    @Test
    public void shouldPersist() throws InterruptedException {
        sortedSet.add(BRAZIL, 10);
        sortedSet.expire(Duration.ofSeconds(1));
        sortedSet.persist();
        Thread.sleep(2_000L);
        assertTrue(sortedSet.size() == 1);
    }

    @Test
    public void shouldRange() {
        sortedSet.add(BRAZIL, 1);
        sortedSet.add(USA, 2);
        sortedSet.add(ENGLAND, 3);

        assertThat(sortedSet.range(2, 3), contains(Ranking.of(ENGLAND, 3.0)));
    }

    @Test
    public void shoulgetRanges() {
        Ranking brazil = Ranking.of(BRAZIL, 1.0);
        Ranking usa = Ranking.of(USA, 2.0);
        Ranking england = Ranking.of(ENGLAND, 3.0);
        sortedSet.add(brazil);
        sortedSet.add(usa);
        sortedSet.add(england);

        assertThat(sortedSet.getRanking(), contains(brazil, usa, england));
    }

    @Test
    public void shouldRevRange() {
        sortedSet.add(BRAZIL, 1);
        sortedSet.add(USA, 2);
        sortedSet.add(ENGLAND, 3);

        assertThat(sortedSet.revRange(2, 3), contains(Ranking.of(BRAZIL, 1.0)));
    }

    @Test
    public void shoulgetRevRanges() {
        Ranking brazil = Ranking.of(BRAZIL, 1.0);
        Ranking usa = Ranking.of(USA, 2.0);
        Ranking england = Ranking.of(ENGLAND, 3.0);
        sortedSet.add(brazil);
        sortedSet.add(usa);
        sortedSet.add(england);

        assertThat(sortedSet.getRevRanking(), contains(england, usa, brazil));
    }

}