package tp.myapp.ejb.impl;

import javax.ejb.Stateless;

import tp.myapp.ejb.itf.IConvertisseur;

@Stateless
public class ConvertisseurBean implements IConvertisseur{

	@Override
	public double euroToFranc(double montant) {
		return montant * 6.55957;
	}

}
