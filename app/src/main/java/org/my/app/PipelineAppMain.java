/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @authors Andrew Dinn
 */

package org.my.app;

import org.my.pipeline.core.PipelineProcessor;
import org.my.pipeline.core.SinkProcessor;
import org.my.pipeline.core.SourceProcessor;
import org.my.pipeline.impl.FileSource;
import org.my.pipeline.impl.FileSink;
import org.my.pipeline.impl.PatternReplacer;
import org.my.pipeline.impl.TraceProcessor;

import java.io.IOException;

/**
 * A simple application which streams an input file, performs several pattern matching replacements
 * on each line in turn and writes the transformed text to an output file. The data stream is
 * routed through TraceProcessors showing each successive stage of data transformation on System.out.
 */
public class PipelineAppMain
{
    public static void main(String[] args)
    {
        try {
            // pipeline source reads file foo.txt
            SourceProcessor reader = new FileSource("foo.txt");
            TraceProcessor tracein = new TraceProcessor("in: ", reader);
            PipelineProcessor[] pipeline = new PipelineProcessor[5];

            // pipeline stage 0 replaces login name
            pipeline[0] = new PatternReplacer("adinn", "msmith", tracein);
            // pipeline stage 1 tees intermediate output to a trace filewriter
            pipeline[1] = new TraceProcessor("mid1: ", pipeline[0]);
            // pipeline stage 2 replaces first name
            pipeline[2] = new PatternReplacer("[Aa]ndrew", "Michael", pipeline[1]);
            // pipeline stage 3 tees intermediate output to a trace filewriter
            pipeline[3] = new TraceProcessor("mid2: ", pipeline[2]);
            // pipeline stage 4 replaces surname
            pipeline[4] = new PatternReplacer("(.*)[Dd]inn(.*)", "\\1Smith\\2", pipeline[3]);

            // pipeline stage 4 feeds a final TraceProcessor displaying the output
            TraceProcessor traceout = new TraceProcessor("out: ", pipeline[4]);
            // the last TraceProcessor writes the final output to filebar.txt
            SinkProcessor writer = new FileSink("bar.txt", traceout);
            // start all the stream processors
            reader.start();
            tracein.start();
            for(int i = 0; i <pipeline.length ;i++) {
                pipeline[i].start();
            }
            traceout.start();
            writer.start();
            //now wait for all the processors to finish
            reader.join();
            tracein.join();
            for(int i = 0; i <pipeline.length; i++) {
                pipeline[i].join();
            }
            traceout.join();
            writer.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
