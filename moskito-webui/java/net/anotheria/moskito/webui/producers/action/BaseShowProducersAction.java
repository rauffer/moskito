/*
 * $Id$
 * 
 * This file is part of the MoSKito software project
 * that is hosted at http://moskito.dev.java.net.
 * 
 * All MoSKito files are distributed under MIT License:
 * 
 * Copyright (c) 2006 The MoSKito Project Team.
 * 
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), 
 * to deal in the Software without restriction, 
 * including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit 
 * persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice
 * shall be included in all copies 
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */	
package net.anotheria.moskito.webui.producers.action;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.bean.FormBean;
import net.anotheria.moskito.core.producers.IStatsProducer;
import net.anotheria.moskito.webui.producers.api.ProducerAPI;
import net.anotheria.moskito.webui.producers.api.UnitCountAO;
import net.anotheria.moskito.webui.shared.action.BaseMoskitoUIAction;
import net.anotheria.moskito.webui.shared.bean.GraphDataBean;
import net.anotheria.moskito.webui.shared.bean.NaviItem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base action for producers presentation action.
 * @author lrosenberg.
 */
public abstract class BaseShowProducersAction extends BaseMoskitoUIAction {

	private static ProducerAPI producerAPI = APIFinder.findAPI(ProducerAPI.class);


	protected ProducerAPI getProducerAPI(){
		return producerAPI;
	}
	/**
	 * Returns the list of producers for presentation.
	 * @param req
	 * @return
	 */
	protected abstract List<IStatsProducer> getProducers(HttpServletRequest req);
	/**
	 * Returns the page title. 
	 * @param req
	 * @return
	 */
	public abstract String getPageTitle(HttpServletRequest req);
	
	@Override
	public ActionCommand execute(ActionMapping mapping, FormBean formBean, HttpServletRequest req, HttpServletResponse res) {

		Map<String, GraphDataBean> graphData = new HashMap<String, GraphDataBean>();

		req.setAttribute("decorators", getDecoratedProducers(req, getProducers(req), graphData));
		req.setAttribute("graphDatas", graphData.values());

		doCustomProcessing(req, res);
		
		req.setAttribute("pageTitle", getPageTitle(req));

		if (getForward(req).equalsIgnoreCase("csv")){
			res.setHeader("Content-Disposition", "attachment; filename=\"producers.csv\"");
		}

		return mapping.findCommand( getForward(req) );
	}

	private static final UnitCountAO EMPTY_UNIT = new UnitCountAO("Select ", 0);

	protected void doCustomProcessing(HttpServletRequest req, HttpServletResponse res){
		try{
			List<UnitCountAO> categories = getProducerAPI().getCategories();
			categories.add(0, EMPTY_UNIT);
			req.setAttribute("categories", categories);

			List<UnitCountAO> subsystems = getProducerAPI().getSubsystems();
			categories.add(0, EMPTY_UNIT);
			req.setAttribute("subsystems", subsystems);
		}catch(APIException e){
			throw new IllegalStateException("Couldn't obtain categories/subsystems ", e);
		}
	}

	@Override
	protected final NaviItem getCurrentNaviItem() {
		return NaviItem.PRODUCERS;
	}

}