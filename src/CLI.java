import lib_gen.Adduct;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import compound_discoverer.CDPeakFinder;

import javax.swing.JProgressBar;


@Command(name = "LipiDex CLI App", mixinStandardHelpOptions = true, version = "1.0",
        description = "Prints the checksum (MD5 by default) of a file to STDOUT.")
public class CLI implements Callable<Integer> {

    // Command line arguments can be separated into options and positional parameters:
    //      Options have a name,
    //      positional parameters are usually the values that follow the options, but they may be mixed.

    @Option(names = {"-a", "--aligned"}, description = "Filepath Compound Discoverer Aligned Peak Table (.csv)", required = true)
    Path alignedTable;

    @Option(names = {"-u", "--unaligned"}, description = "Compound Discoverer Unaligned Peak Table (.csv)", required = true)
    Path unalignedTable;

    @Option(names = {"-r", "--results"}, description = "input Results.csv files from Spectrum Searcher", required = true)
    ArrayList<File> ssResultsFilepaths;

    @Override
    public Integer call() throws Exception {

        JProgressBar dummyProgressBar = new JProgressBar();
        ArrayList<Integer> dummySamplePairNumbers = new ArrayList<>();
        ArrayList<Adduct> dummyAdductsDB = new ArrayList<Adduct>();
        // the Default Adducts DB is located at src/peak_finder/Possible_Adducts.csv
        // the default Adducts DB is read using PeakFinderGUI.readAdducts(Filename)



        // Have to convert ssResultsFilepaths into the required "File" type
        // https://stackoverflow.com/questions/6903335/java-path-vs-file
        // File is older, Path is newer addition to Java
        // Path.toFile()

        CDPeakFinder cliCDPeakFinder = new CDPeakFinder(
                alignedTable.toString(),
                unalignedTable.toString(),
                ssResultsFilepaths,
                2,
                true,
                2.0,
                dummyProgressBar,
                dummySamplePairNumbers,
                dummyAdductsDB);

        cliCDPeakFinder.runQuantitation(true, true, true);

        return 0;
    }

    public static void main(String[] args) {
        CLI my_CLI = new CLI();
        CommandLine my_CL = new CommandLine(my_CLI);
        int exitCode = my_CL.execute(args);
//        int exitCode = new CommandLine(my_CLI).execute(args);
        System.exit(exitCode);
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