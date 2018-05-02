/*
 * Copyright (C) 2018 Tomas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package UserInterface;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Tomas
 */
@Entity
@Table(name = "company", catalog = "stockmarket", schema = "")
@NamedQueries({
    @NamedQuery(name = "Company.findAll", query = "SELECT c FROM Company c")
    , @NamedQuery(name = "Company.findBySymbol", query = "SELECT c FROM Company c WHERE c.symbol = :symbol")
    , @NamedQuery(name = "Company.findByCoName", query = "SELECT c FROM Company c WHERE c.coName = :coName")
    , @NamedQuery(name = "Company.findByCoCEO", query = "SELECT c FROM Company c WHERE c.coCEO = :coCEO")
    , @NamedQuery(name = "Company.findByCoWebsite", query = "SELECT c FROM Company c WHERE c.coWebsite = :coWebsite")
    , @NamedQuery(name = "Company.findByCoMarket", query = "SELECT c FROM Company c WHERE c.coMarket = :coMarket")
    , @NamedQuery(name = "Company.findByCoSector", query = "SELECT c FROM Company c WHERE c.coSector = :coSector")
    , @NamedQuery(name = "Company.findByCoIndustry", query = "SELECT c FROM Company c WHERE c.coIndustry = :coIndustry")
    , @NamedQuery(name = "Company.findByCoValue", query = "SELECT c FROM Company c WHERE c.coValue = :coValue")})
public class Company implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Symbol")
    private String symbol;
    @Basic(optional = false)
    @Column(name = "coName")
    private String coName;
    @Column(name = "coCEO")
    private String coCEO;
    @Column(name = "coWebsite")
    private String coWebsite;
    @Column(name = "coMarket")
    private String coMarket;
    @Column(name = "coSector")
    private String coSector;
    @Column(name = "coIndustry")
    private String coIndustry;
    @Lob
    @Column(name = "coDescription")
    private String coDescription;
    @Basic(optional = false)
    @Column(name = "coValue")
    private double coValue;

    public Company() {
    }

    public Company(String symbol) {
        this.symbol = symbol;
    }

    public Company(String symbol, String coName, double coValue) {
        this.symbol = symbol;
        this.coName = coName;
        this.coValue = coValue;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        String oldSymbol = this.symbol;
        this.symbol = symbol;
        changeSupport.firePropertyChange("symbol", oldSymbol, symbol);
    }

    public String getCoName() {
        return coName;
    }

    public void setCoName(String coName) {
        String oldCoName = this.coName;
        this.coName = coName;
        changeSupport.firePropertyChange("coName", oldCoName, coName);
    }

    public String getCoCEO() {
        return coCEO;
    }

    public void setCoCEO(String coCEO) {
        String oldCoCEO = this.coCEO;
        this.coCEO = coCEO;
        changeSupport.firePropertyChange("coCEO", oldCoCEO, coCEO);
    }

    public String getCoWebsite() {
        return coWebsite;
    }

    public void setCoWebsite(String coWebsite) {
        String oldCoWebsite = this.coWebsite;
        this.coWebsite = coWebsite;
        changeSupport.firePropertyChange("coWebsite", oldCoWebsite, coWebsite);
    }

    public String getCoMarket() {
        return coMarket;
    }

    public void setCoMarket(String coMarket) {
        String oldCoMarket = this.coMarket;
        this.coMarket = coMarket;
        changeSupport.firePropertyChange("coMarket", oldCoMarket, coMarket);
    }

    public String getCoSector() {
        return coSector;
    }

    public void setCoSector(String coSector) {
        String oldCoSector = this.coSector;
        this.coSector = coSector;
        changeSupport.firePropertyChange("coSector", oldCoSector, coSector);
    }

    public String getCoIndustry() {
        return coIndustry;
    }

    public void setCoIndustry(String coIndustry) {
        String oldCoIndustry = this.coIndustry;
        this.coIndustry = coIndustry;
        changeSupport.firePropertyChange("coIndustry", oldCoIndustry, coIndustry);
    }

    public String getCoDescription() {
        return coDescription;
    }

    public void setCoDescription(String coDescription) {
        String oldCoDescription = this.coDescription;
        this.coDescription = coDescription;
        changeSupport.firePropertyChange("coDescription", oldCoDescription, coDescription);
    }

    public double getCoValue() {
        return coValue;
    }

    public void setCoValue(double coValue) {
        double oldCoValue = this.coValue;
        this.coValue = coValue;
        changeSupport.firePropertyChange("coValue", oldCoValue, coValue);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (symbol != null ? symbol.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Company)) {
            return false;
        }
        Company other = (Company) object;
        if ((this.symbol == null && other.symbol != null) || (this.symbol != null && !this.symbol.equals(other.symbol))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserInterface.Company[ symbol=" + symbol + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
