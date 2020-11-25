/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2020 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2020 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.nephron.catheter;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import com.google.common.base.MoreObjects;

public class Flow {
    private final Instant start;
    private final Instant end;

    /** Time when flow was last reported **/
    private Instant reported;

    /** Bytes transmitted since last report **/
    private long bytes;

    public Flow(final Instant start,
                final Instant end) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);

        this.reported = start;
        this.bytes = 0;
    }

    public Instant getStart() {
        return this.start;
    }

    public Instant getEnd() {
        return this.end;
    }

    public boolean checkTimeout(final Instant now, final Duration activeTimeout) {
        return !this.reported.plus(activeTimeout).isAfter(now);
    }

    public boolean checkFinished(final Instant now) {
        return !this.end.isAfter(now);
    }

    protected FlowReport report(final Instant now) {
        // Create report of current stats
        // Report the real flow end if the flow has ended
        final FlowReport report = new FlowReport(this.reported,
                                                 this.end.isBefore(now) ? this.end : now,
                                                 this.bytes);

        // Reset the stats
        this.reported = now;
        this.bytes = 0;

        return report;
    }

    protected void transmit(final long bytes) {
        this.bytes += bytes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("start", this.start)
                          .add("end", this.end)
                          .add("lastReported", this.reported)
                          .add("bytes", this.bytes)
                          .toString();
    }
}