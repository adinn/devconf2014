/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat and individual contributors as identified
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

package org.my.pipeline.impl;

import org.my.pipeline.core.SinkProcessor;
import org.my.pipeline.core.Source;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileWriter is a data Sink which writes the characters from its input stream to a file
 */
public class FileWriter extends SinkProcessor {
    private FileOutputStream fout;

    public FileWriter(String file, Source source) throws IOException
    {
        super(source);
        this.fout = new FileOutputStream(file);
    }

    public void run()
    {
        if (input==null || fout == null) {
            //nothing to do
            return;
        }

        try {
            int next = input.read();
            while  (next >= 0) {
                fout.write(next);
                next = input.read();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                fout.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
