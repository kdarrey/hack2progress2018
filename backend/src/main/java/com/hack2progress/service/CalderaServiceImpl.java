package com.hack2progress.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hack2progress.constants.Constants;
import com.hack2progress.enumeraciones.TipoCalderaEnum;
import com.hack2progress.model.dto.CalculoDTO;
import com.hack2progress.model.dto.response.CalderaResponse;
import com.hack2progress.util.Util;

@Service
public class CalderaServiceImpl implements CalderaService {

	@Autowired
	private Util util;
	
	@Override
	public List<CalderaResponse> calcular(CalculoDTO calculo) {
		Double valorZC = util.getValorZonaClimatica(calculo.getPosicion().getLon(), calculo.getPosicion().getLat());
		Double potenciaCaldera = (calculo.getSuperficie() * calculo.getValorOrientacion() * calculo.getValorAislamiento() * valorZC * 85)/1000;
		
		List<CalderaResponse> listaCalderaResponse = new ArrayList<>();
		for(TipoCalderaEnum tipoCaldera: TipoCalderaEnum.values()) {
			CalderaResponse caldera = new CalderaResponse();
			caldera.setTipoCaldera(tipoCaldera.getNombre());
			caldera.setPotencia(potenciaCaldera);
			switch(tipoCaldera) {
				case BIOMASA:
					caldera.setInversionInicialEstimada(new Double("6500"));
					Double gastoAnualEstimadoBiomasa=((((potenciaCaldera/Constants.POTENCIA_CALORIFICA_PELLET_HORA)*Constants.ESTIMACION_CALDERA_HORAS_ENCENDIDA_POR_DIA)
							*Constants.DIAS_POR_ANHO)
							/Constants.PESO_SACO_PELLET)
							*Constants.PRECIO_SACO_PELLET;
					caldera.setGastoAnualEstimado(gastoAnualEstimadoBiomasa);
				break;
				case ELECTRICA:
					//las calderas electricas solo pueden tener minimo de 20kw de potencia, porque sino no son capaces de calentar acs
					if(potenciaCaldera<20) {
						caldera.setPotencia(new Double("20"));
					}
					caldera.setInversionInicialEstimada(new Double("1000"));
					Double gastoAnualEstimadoElectrico = (((potenciaCaldera*Constants.PRECIO_ELECTRICIDAD_KW_HORA)*Constants.ESTIMACION_CALDERA_HORAS_ENCENDIDA_POR_DIA)*Constants.DIAS_POR_ANHO);
					caldera.setGastoAnualEstimado(gastoAnualEstimadoElectrico);
				break;
				case GAS:
					//las calderas de gas solo pueden tener minimo de 20kw de potencia, porque sino no son capaces de calentar acs
					if(potenciaCaldera<20) {
						caldera.setPotencia(new Double("20"));
					}
					caldera.setInversionInicialEstimada(new Double("1000"));
					Double gastoAnualEstimadoGas = (((potenciaCaldera/Constants.POTENCIA_CALORIFICA_GAS_PROPANO)*Constants.ESTIMACION_CALDERA_HORAS_ENCENDIDA_POR_DIA)*Constants.PRECIO_KG_PROPANO)*Constants.DIAS_POR_ANHO;
					caldera.setGastoAnualEstimado(gastoAnualEstimadoGas);
				break;
				case GAS_NATURAL:
					//las calderas de gas natural solo pueden tener minimo de 20kw de potencia, porque sino no son capaces de calentar acs
					if(potenciaCaldera<20) {
						caldera.setPotencia(new Double("20"));
					}
					caldera.setInversionInicialEstimada(new Double("1000"));
					Double gastoAnualEstimadoGasNatural = (((potenciaCaldera/Constants.POTENCIA_CALORIFICA_GAS_NATURAL)*Constants.ESTIMACION_CALDERA_HORAS_ENCENDIDA_POR_DIA)*Constants.PRECIO_GAS_NATURAL_KWH)*Constants.DIAS_POR_ANHO;
					caldera.setGastoAnualEstimado(gastoAnualEstimadoGasNatural);
				break;
				case GASOLEO:
					//las calderas de gasoleo solo pueden tener minimo de 20kw de potencia, porque sino no son capaces de calentar acs
					if(potenciaCaldera<20) {
						caldera.setPotencia(new Double("20"));
					}
					caldera.setInversionInicialEstimada(new Double("2500"));
					Double gastoAnualEstimadoGasoleo = (((potenciaCaldera/Constants.POTENCIA_CALORIFICA_GASOLEO)*Constants.ESTIMACION_CALDERA_HORAS_ENCENDIDA_POR_DIA)*Constants.PRECIO_GASOLEO_CALEFACCION)*Constants.DIAS_POR_ANHO;
					caldera.setGastoAnualEstimado(gastoAnualEstimadoGasoleo);
				break;
			}
			listaCalderaResponse.add(caldera);	
		}
		return listaCalderaResponse;
	}

}
