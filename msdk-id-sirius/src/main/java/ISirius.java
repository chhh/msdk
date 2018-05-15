import de.unijena.bioinf.ChemistryBase.chem.Ionization;
import de.unijena.bioinf.ChemistryBase.chem.PrecursorIonType;
import de.unijena.bioinf.ChemistryBase.ms.Ms2Experiment;
import de.unijena.bioinf.ChemistryBase.ms.Peak;
import de.unijena.bioinf.ChemistryBase.ms.Spectrum;
import de.unijena.bioinf.sirius.IdentificationResult;
import de.unijena.bioinf.sirius.Sirius;
import io.github.msdk.MSDKException;
import io.github.msdk.io.msp.MspImportAlgorithm;
import io.github.msdk.io.msp.MspSpectrum;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by evger on 14-May-18.
 */

public class ISirius {

  private static class Pair<K, V> {
    final K key;
    final V val;

    public Pair(K key, V val) {
      this.key = key;
      this.val = val;
    }
  }

  /* This function is left here for non-msp files */
  private static Pair<double[], double[]> readCustomMsFile(String path) throws IOException {
    Scanner sc = new Scanner(new File(path));
    ArrayList<String> strings = new ArrayList<>();
    while (sc.hasNext()) {
      strings.add(sc.nextLine());
    }
    sc.close();

    double mz[] = new double[strings.size()];
    double intensive[] = new double[strings.size()];

    int index = 0;
    for (String s : strings) {
      String[] splitted = s.split("\t");
      mz[index] = Double.parseDouble(splitted[0]);
      intensive[index++] = Double.parseDouble(splitted[1]);
    }

    return new Pair<>(mz, intensive);
  }

  public static List<IdentificationResult> identifyMs2Spectrum(String path)
      throws MSDKException, IOException {
    final Sirius sirius = new Sirius();

    /**
     *
     * TODO: Temporary added dependency back to the 3.1.3
     * sirius_api:4.0 (as well as sirius:4.0) does not have those classes
     *
     **/

    /* TODO: remove fixed value */
    double parentMass = 231.065;
    double mz[], intensive[];

    /*
      Pair<double[], double[]> content = readCustomMsFile(path);
      mz = content.getValue0();
      intensive = content.getValue1();
    */

    // Problem arises with Accept by string & does not appear with Accept by file
    File inputFile = new File(path);
    MspSpectrum mspSpectrum = MspImportAlgorithm.parseMspFromFile(inputFile);

    mz = mspSpectrum.getMzValues();
    intensive = LocalArrayUtil.convertToDoubles(mspSpectrum.getIntensityValues());
    Spectrum<Peak> ms2 = sirius.wrapSpectrum(mz, intensive);

    /**
     *  TODO: explore non-deprecated methods
     *
     **/
    Ionization ion = sirius.getIonization("[M+H]+");
    PrecursorIonType precursor = sirius.getPrecursorIonType("[M+H]+");
    Ms2Experiment experiment = sirius.getMs2Experiment(parentMass, precursor, null, ms2);

//    sirius.getMs2Experiment()

//        Compilation failures as no ms1 spectrum, right now do not understand how not to set it.
//        Error on request for GurobiJni60 library
    /* Runtime failure on fragmentation tree construction (NullPointer) - if used on MSMS provided by Tomas earlier */
    /* Runtime failure on fragmentation tree construction (assertion error) - if used on data from .msp */

    List<IdentificationResult> results = sirius.identify(experiment, 10);
    return results;
  }
}
