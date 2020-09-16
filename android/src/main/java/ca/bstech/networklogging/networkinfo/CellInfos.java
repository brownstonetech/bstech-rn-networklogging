package ca.bstech.networklogging.networkinfo;

import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;

import java.util.ArrayList;
import java.util.List;

public class CellInfos {
    private List<CellInfoLte> cellInfoLteList = new ArrayList<>();
    private List<CellInfoNr> cellInfoNrList = new ArrayList<>();

    public void addCellInfoLte(CellInfoLte cellInfoLte) {
        if ( cellInfoLteList == null ) cellInfoLteList = new ArrayList<>();
        cellInfoLteList.add(cellInfoLte);
    }

    public void addCellInfoNr(CellInfoNr cellInfoNr) {
        if ( cellInfoNrList == null ) cellInfoNrList = new ArrayList<>();
        cellInfoNrList.add(cellInfoNr);
    }

    public List<CellInfoLte> getCellInfoLte() {
        return cellInfoLteList;
    }

    public List<CellInfoNr> getCellInfoNr() {
        return cellInfoNrList;
    }
}
