/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, 2014 Red Hat and individual contributors as identified
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
import org.my.pipeline.core.TeeProcessor;
import org.my.pipeline.impl.FileSource;
import org.my.pipeline.impl.FileSink;
import org.my.pipeline.impl.PatternReplacer;

import java.io.IOException;

/**
 * A variant of PipelineAppMain which uses TeeProcessors to dump intermediate output to
 * files.
 */
public class PipelineAppMain1
{
    public static void main(String[] args)
    {
        try {
            // pipeline source reads file foo.txt
            SourceProcessor reader = new FileSource("foo.txt");
            PipelineProcessor[] pipeline = new PipelineProcessor[5];

            // pipeline stage 0 replaces login name
            pipeline[0] = new PatternReplacer("adinn", "msmith",reader);
            // pipeline stage 1 tees intermediate output to a trace filewriter
            pipeline[1] = new TeeProcessor(pipeline[0]);
            // pipeline stage 2 replaces first name
            pipeline[2] = new PatternReplacer("[Aa]ndrew", "Michael", pipeline[1]);
            // pipeline stage 3 tees intermediate output to a trace filewriter
            pipeline[3] = new TeeProcessor(pipeline[2]);
            // pipeline stage 4 replaces surname
            pipeline[4] = new PatternReplacer("(.*)[Dd]inn(.*)", "\\1Smith\\2", pipeline[3]);

            // the tees feed file wwriters sowe can sanity check the intermediate results
            SinkProcessor writer = new FileSink("bar1.txt", pipeline[1]);
            SinkProcessor writer2 = new FileSink("bar2.txt", pipeline[3]);

            // pipeline stage 4 writes the final output to filebar.txt
            SinkProcessor writer3 = new FileSink("bar.txt", pipeline[4]);
            // start all the stream processors
            reader.start();
            for(int i = 0; i <pipeline.length ;i++) {
                pipeline[i].start();
            }
            writer.start();
            writer2.start();
            writer3.start();
            //now wait for all the processors to finish
            reader.join();
            for(int i = 0; i <pipeline.length; i++) {
                pipeline[i].join();
            }
            writer.join();
            writer2.join();
            writer3.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
