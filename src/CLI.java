import lib_gen.Adduct;
import mzmine.MzPeakFinder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import compound_discoverer.CDPeakFinder;

import javax.swing.JProgressBar;


@Command(name = "LipiDex CLI App", mixinStandardHelpOptions = true, version = "0.1.0",
        description = "Command Line Interface for LipiDex")
public class CLI implements Callable<Integer> {

    // Command line arguments can be separated into options and positional parameters:
    //      Options have a name,
    //      positional parameters are usually the values that follow the options, but they may be mixed.

    @Option(names = {"-t", "--type"},
            description = "Choose <CD> for Compound Discoverer or <MZ> for MZMine", required = true)
    String CDorMZ;

    @Option(names = {"-f", "--firsttable"},
            description = "Filepath CD Aligned Table OR MZMine Positive Table", required = true)
    Path firstTable;

    @Option(names = {"-s", "--secondtable"},
            description = "Filepath CD Unaligned Table OR MZMine Negative Table")
    Path secondTable;

    @Option(names = {"-r", "--results"},
            description = "Filepaths to Results.csv files from Spectrum Searcher", required = true)
    ArrayList<File> ssResultsFilepaths;

    @Option(names = {"-p", "--results-files-prefix"},
            description = "Results Filenames prefix (to prevent overlapping Final_Results.csv names")
    String resultsFilesPrefix;

    @Option(names = {"--path-to-results"},
            description = "Folder where Results Files will be written")
    String pathToResultsString;

//    @Option(names = {"--sep-pol-filt"},
//    description = "Boolean separate polarity filtering, default true")
//    Boolean sepPol = true;

    @Option(names = {"--use-new-parser"},
            description = "true/false to use scripting node automatic parser")
    Boolean useNewParser;

    @Override
    public Integer call() throws Exception {

        JProgressBar dummyProgressBar = new JProgressBar();
        ArrayList<Integer> dummySamplePairNumbers = new ArrayList<>();  // TODO: Add Sample Pair numbers param
        ArrayList<Adduct> adductsDB = new ArrayList<Adduct>();          // TODO: Create parameter to add more adducts
        populateAdductsDB("src/peak_finder/Possible_Adducts.csv", adductsDB);

        // the Default Adducts DB is located at src/peak_finder/Possible_Adducts.csv
        // the default Adducts DB is read using PeakFinderGUI.readAdducts(Filename)

        Path pathToResults = Paths.get(pathToResultsString);
        System.out.println(pathToResults.toString());

        if (CDorMZ.equals("CD")) {
            CDPeakFinder cliCDPeakFinder = new CDPeakFinder(
                    firstTable.toString(),
                    secondTable.toString(),
                    ssResultsFilepaths,
                    2,
                    true,
                    2.0,
                    dummyProgressBar,
                    dummySamplePairNumbers,
                    adductsDB);

            cliCDPeakFinder.runQuantitation(true,
                    true,
                    true,
                    true);  // TODO: Add filtering boolean Parameters

            return 0;
        }
        else if (CDorMZ.equals("MZ")){
            MzPeakFinder cliMZPeakFinder = new MzPeakFinder(
                    firstTable.toString(),
                    secondTable.toString(),
                    ssResultsFilepaths,
                    2,
                    true,
                    2.0,
                    dummyProgressBar,
                    dummySamplePairNumbers,
                    adductsDB,
                    resultsFilesPrefix,
                    pathToResults);

            cliMZPeakFinder.runQuantitation(true, true, true);

            return 0;
        }
        else {
            return -1;
        }
    }

    public static void main(String[] args) {
        CLI my_CLI = new CLI();
        CommandLine my_CL = new CommandLine(my_CLI);
        int exitCode = my_CL.execute(args);
//        int exitCode = new CommandLine(my_CLI).execute(args);
        System.exit(exitCode);
    }

    private static void populateAdductsDB(String filename, ArrayList<Adduct> adductsDB) throws IOException
    {
        String line = null;
        String [] split = null;
        String name;
        String formula;
        Boolean loss;
        String polarity;
        int charge;

        //Create file buffer
//        File file = new File(filename);
        InputStream stream = CLI.class.getResourceAsStream("peak_finder/Possible_Adducts.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

//        File streamFile = new File(getClass().getResource(filename).toURI());

//        FileInputStream inputStream = new FileInputStream(new File(getClass().getResource(filename).toURI()));

//        BufferedReader streamReader = new BufferedReader(stream);

//        //Clear adducts DB
//        adductsDB = new ArrayList<Adduct>();

        //read line if not empty
        while ((line = reader.readLine()) != null)
        {
            if (!line.contains("Name"))
            {
                split = line.split(",");

                name = split[0];

                formula = split[1];

                if (split[2].equals("FALSE")) loss = false;
                else {loss = true;}

                polarity = split[3];
                charge = Integer.valueOf(split[4]);

                adductsDB.add(new Adduct(name, formula, loss, polarity, charge));
            }
        }
        reader.close();
    }
}







/* Parameters needed for CD
 *Required:
 * 	aligned table
 * 	unaligned table
 * 	Spec Search Results files
 *
 *Optional:
 * 	MS/MS Filtering Params:
 * 		Min Lipid Spectral Purity (default 75)
 * 		Min MS2 Search score (default 500)
 * 		Min MS2 Search reverse Score (default 700)
 * 	Feature Association Params:
 * 		FWHM Window (default 2.0)
 * 		Max Mass Diff ppm (default 15)
 * 	Result Filtering Params:
 * 		Adduct/Dimer Boolean (default True)
 * 			database of adducts to search
 * 		In-source Frags Boolean (default True)
 * 		Max RT M.A.D (default 3.5)
 * 		Feature Found in N files (default 2)
 *
 * New Parameters for user-friendliness:
 * 	Final Results filename prefix (to prevent all files from being named Final Results)
 * 	Path folder for Final Results files (to prevent overwriting previous files)
 *
 *
 *
 *
 *
 *
 * */