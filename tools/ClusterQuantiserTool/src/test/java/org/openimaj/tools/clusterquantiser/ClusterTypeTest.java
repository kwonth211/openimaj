/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.tools.clusterquantiser;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.openimaj.data.RandomData;
import org.openimaj.feature.local.list.FileLocalFeatureList;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.io.IOUtils;
import org.openimaj.ml.clustering.Cluster;
import org.openimaj.tools.clusterquantiser.ClusterQuantiser;
import org.openimaj.tools.clusterquantiser.ClusterQuantiserOptions;
import org.openimaj.tools.clusterquantiser.ClusterType;
import org.openimaj.tools.clusterquantiser.Precision;
import org.openimaj.util.array.ByteArrayConverter;



public class ClusterTypeTest {
	
	private int nterms;
	private int nDocs;
	private int[][] data;
	private File inputFile;
	private String[] inputKeyFiles;

	@Before
	public void setup() {
		nterms = 100;
		nDocs = 100;
		data = new int[nDocs][];
		for(int i = 0; i < nDocs; i ++ ){
			data[i] = new int[nterms];
			for(int j = 0; j < nterms; j++){
				data[i][j] = new Random().nextInt();
			}
		}
		String[] inputKeySets = new String[]{
				"ukbench00000.key",
				"ukbench00001.key",
				"ukbench00002.key",
				"ukbench00003.key",
				"ukbench00004.key"
		};
		inputKeyFiles = new String[inputKeySets.length];
		try {
			inputFile = File.createTempFile("inputFile", ".txt");
			PrintWriter pw = new PrintWriter(new FileOutputStream(inputFile));
			int i = 0;
			for(String keyFile : inputKeySets){
				try
				{
					File f = new File(
						new URI(this.getClass().getResource("keys/"+keyFile).toString()).getPath());
					pw.println(f.getAbsolutePath());
					inputKeyFiles[i++] = f.getAbsolutePath();
				}
				catch( URISyntaxException e )
				{
					e.printStackTrace();
				}
			}
			pw.flush();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test public void testFastKMeansInit() throws Exception{
//		File randomFile = File.createTempFile("randomset", ".voc");
//		File fkmeansFile = File.createTempFile("fastkmeans", ".voc");
//		String[] intClusterArgs = new String[]{
//				"-ct","RANDOMSET",
//				"-c",randomFile.getAbsolutePath(),
//				"-t","LOWE_KEYPOINT_ASCII",
//				"-s","5",
//				"-k","5",
//				"-rs","1",
//				"-crs","1",
//				"-p","BYTE",
//				inputKeyFiles[0],inputKeyFiles[1],inputKeyFiles[2]};
////		ClusterQuantiserOptions intClusterCop = ClusterQuantiser.mainOptions(intClusterArgs);
////		Cluster<?> intCluster = ClusterQuantiser.do_create(intClusterCop);
////		intClusterArgs = new String[]{
////				"-ct","FASTKMEANS",
////				"-c",fkmeansFile.getAbsolutePath(),
////				"-t","LOWE_KEYPOINT_ASCII",
////				"-s","5",
////				"-k","5",
////				"-rs","1",
////				"-crs","1",
////				"-p","BYTE",
////				"-cin","RANDOMSETCLUSTER",
////				"-rss",randomFile.getAbsolutePath(),
////				inputKeyFiles[0],inputKeyFiles[1],inputKeyFiles[2]};
////		intClusterCop = ClusterQuantiser.mainOptions(intClusterArgs);
////		intCluster = ClusterQuantiser.do_create(intClusterCop);
//		intClusterArgs = new String[]{
//				"-ct","FASTKMEANS",
//				"-c",fkmeansFile.getAbsolutePath(),
//				"-t","LOWE_KEYPOINT_ASCII",
//				"-bs","-sf","/Volumes/memling/ukbench/samples/batch-samples-ALL-sift-intensity.samples",
//				"-k","10",
//				"-rs","1",
//				"-crs","1",
//				"-p","BYTE",
//				"-cin","RANDOMSETCLUSTER",
//				"-rss","/Volumes/memling/ukbench/newcodebooks/10/sift-intensity/randomsetbyte.voc",
//				inputKeyFiles[0],inputKeyFiles[1],inputKeyFiles[2]};
//		ClusterQuantiserOptions intClusterCop = ClusterQuantiser.mainOptions(intClusterArgs);
//		Cluster<?> intCluster = ClusterQuantiser.do_create(intClusterCop);
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testClusterTypePrecision() throws IOException, InterruptedException, CmdLineException{
		// given the same random seed all clusters should quantise to the same value regardless of precision
		List<Keypoint> kpl = FileLocalFeatureList.read(new File(inputKeyFiles[0]), Keypoint.class);
		for(ClusterType ct : ClusterType.values()){
			if(!ct.equals(ClusterType.FASTKMEANS) && !ct.equals(ClusterType.RANDOM) && !ct.equals(ClusterType.RANDOMSET) ) continue;
			System.out.println ("TESTING CLUSTER TYPE: " + ct);
			File tempFile = File.createTempFile("cluster", ".voc");
			String[] intClusterArgs = new String[]{
					"-ct",ct.toString(),
					"-c",tempFile.getAbsolutePath(),
					"-t","LOWE_KEYPOINT_ASCII",
					"-s","5",
					"-k","5",
					"-rs","1",
					"-crs","1",
					inputKeyFiles[0],inputKeyFiles[1],inputKeyFiles[2]};
			ClusterQuantiserOptions intClusterCop = ClusterQuantiser.mainOptions(intClusterArgs);
			Cluster<?,?> intCluster = ClusterQuantiser.do_create(intClusterCop);
			intCluster.optimize(false);
			int[][] intClusterCenters = (int[][]) intCluster.getClusters();
			
			for(Precision p : Precision.values()){
				if(!p.equals(Precision.BYTE) && !p.equals(Precision.INT) ) continue;
				System.out.println ("TESTING PRECISION: " + p);
				File precTempFile = File.createTempFile("preccluster", ".voc");
				String[] precClusterArgs = new String[]{
						"-ct",ct.toString(),
						"-c",precTempFile.getAbsolutePath(),
						"-t","LOWE_KEYPOINT_ASCII",
						"-s","5",
						"-k","5",
						"-rs","1",
						"-crs","1",
						"-p",p.toString(),
						inputKeyFiles[0],inputKeyFiles[1],inputKeyFiles[2]};
				ClusterQuantiserOptions precisionClusterCop = ClusterQuantiser.mainOptions(precClusterArgs);
				Cluster<?,?> precisionCluster = ClusterQuantiser.do_create(precisionClusterCop);
				precisionCluster.optimize(false);
				if(p.equals(Precision.BYTE)){
					int[][] precisionClusterCenters = ByteArrayConverter.byteToInt((byte[][]) precisionCluster.getClusters());
					int i = 0;
					for(int[] precisionClusterCenter : precisionClusterCenters ){
						if(!Arrays.equals(precisionClusterCenter,intClusterCenters[i]))
							System.err.println("BYTE version has different clusters to INT version");
						
						assertTrue(Arrays.equals(precisionClusterCenter,intClusterCenters[i]));
						i++;
					}
					for(int j = 0; j < 100; j++){
						byte[] pushdata = kpl.get(j).ivec;
						int[] intpushdata = ByteArrayConverter.byteToInt(pushdata);
						assertTrue(((Cluster<?,byte[]>)precisionCluster).push_one(pushdata) == ((Cluster<?,int[]>)intCluster).push_one(intpushdata));
					}
					
				}
				else{
					int[][] precisionClusterCenters = (int[][]) precisionCluster.getClusters();
					int i = 0;
					for(int[] precisionClusterCenter : precisionClusterCenters ){
						assertTrue(Arrays.equals(precisionClusterCenter,intClusterCenters[i++]));
					}
					for(int j = 0; j < 100; j++){
						byte[] pushdata = kpl.get(j).ivec;
						int[] intpushdata = ByteArrayConverter.byteToInt(pushdata);
						assertTrue(((Cluster<?,int[]>)precisionCluster).push_one(intpushdata) == ((Cluster<?,int[]>)intCluster).push_one(intpushdata));
					}
				}
				
			}
		}
	}
//	
	@SuppressWarnings("unchecked")
	@Test public void testAllClusterTypes() throws IOException{
		for(ClusterType ct : ClusterType.values()){
			for(Precision p : Precision.values()){
				ct.precision = p;
				File oldout = File.createTempFile("old", ".voc");
				
				byte[][] data = ByteArrayConverter.intToByte(RandomData.getRandomIntArray(10, 10, 0, 20));
				int[] pushdata = RandomData.getRandomIntArray(1, 10, 0, 20)[0];
				
				Cluster<?,?> oldstyle = ct.create(data);
				
				oldstyle.optimize(false);
				IOUtils.writeBinary(oldout, oldstyle);
				ClusterType sniffedType = ClusterType.sniffClusterType(oldout);
				Cluster<?,?> newstyle = IOUtils.read(oldout, sniffedType.getClusterClass());
				newstyle.optimize(false);
				if(newstyle.getClusters() instanceof byte[][]){
					assertTrue(((Cluster<?,byte[]>)newstyle).push_one(ByteArrayConverter.intToByte(pushdata)) == ((Cluster<?,byte[]>)oldstyle).push_one(ByteArrayConverter.intToByte(pushdata)));
				}
				else{
					assertTrue(((Cluster<?,int[]>)newstyle).push_one(pushdata) == ((Cluster<?,int[]>)oldstyle).push_one(pushdata));
				}
			}
		}
	}
	
	private void testAllArgs(String[] args) throws CmdLineException {
//		ClusterQuantiserOptions opt = new ClusterQuantiserOptions(args);
//		opt.prepare();
//		System.out.println(opt );
		try {
			ClusterQuantiser.mainOptions(args);
		} catch (InterruptedException e) {
			throw new CmdLineException(null,e.getMessage());
		}
	}
	
	@Test
	public void testRForest() throws CmdLineException, IOException{
		testAllArgs(new String[]{"-c", File.createTempFile("codebook", ".voc").getAbsolutePath(), "-v","1","-t","LOWE_KEYPOINT_ASCII","-ct","RFOREST","-s","5","-f",inputFile.getAbsolutePath(),"-d","2","-nt","2"});
	}
	
	@Test
	public void testRandomSet() throws CmdLineException, IOException{
		testAllArgs(new String[]{"-c", File.createTempFile("codebook", ".voc").getAbsolutePath(), "-v","1","-t","LOWE_KEYPOINT_ASCII","-ct","RANDOMSET","-s","5","-f",inputFile.getAbsolutePath(),"-k","5"});
//		return IOUtils.read(new File("codebook1000-kmeans-agsift.voc"), RandomIntCluster.class);
	}
}
