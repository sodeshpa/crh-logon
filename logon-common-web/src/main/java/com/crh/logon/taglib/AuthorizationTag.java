/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 */
public class AuthorizationTag extends TagSupport { 
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AuthorizationTag.class);
	
	private Long authorizedFunction;
	
	private Long function = null;
	private Long function2 = null;
	private Long function3 = null;
	private Long function4 = null;
	private Long function5 = null;
	private Long function6 = null;
	private Long function7 = null;
	private Long function8 = null;
	private Long function9 = null;
	private Long function10 = null;
	private Long function11 = null;
	private Long function12 = null;
	private Long function13 = null;
	private Long function14 = null;
	private Long function15 = null;
	private Long function16 = null;
	private Long function17 = null;
	private Long function18 = null;
	private Long function19 = null;
	private Long function20 = null;
	private Long function21 = null;
	private Long function22 = null;
	private Long function23 = null;
	private Long function24 = null;
	private Long function25 = null;
	private Long function26 = null;
	private Long function27 = null;
	private Long function28 = null;
	private Long function29 = null;
	private Long function30 = null;
	private Long function31 = null;
	private Long function32 = null;
	private Long function33 = null;
	private Long function34 = null;
	private Long function35 = null;

	public AuthorizationTag() {
		super();
	}

    public void setFunction(Long function) {
        this.function = function;
    }
    
    public void setFunction2(Long function2) {
    	this.function2 = function2;
    }
    
    public void setFunction3(Long function3) {
    	this.function3 = function3;
    }
    
    public void setFunction4(Long function4) {
    	this.function4 = function4;
    }
    
    public void setFunction5(Long function5) {
    	this.function5 = function5;
    }
    
    public void setFunction6(Long function6) {
    	this.function6 = function6;
    }
    
    public void setFunction7(Long function7) {
    	this.function7 = function7;
    }
    
    public void setFunction8(Long function8) {
    	this.function8 = function8;
    }
    
    public void setFunction9(Long function9) {
    	this.function9 = function9;
    }
    
    public void setFunction10(Long function10) {
    	this.function10 = function10;
    }
    
	public void setFunction11(Long function11) {
		this.function11 = function11;
	}

	public void setFunction12(Long function12) {
		this.function12 = function12;
	}

	public void setFunction13(Long function13) {
		this.function13 = function13;
	}

	public void setFunction14(Long function14) {
		this.function14 = function14;
	}

	public void setFunction15(Long function15) {
		this.function15 = function15;
	}

	public void setFunction16(Long function16) {
		this.function16 = function16;
	}

	public void setFunction17(Long function17) {
		this.function17 = function17;
	}

	public void setFunction18(Long function18) {
		this.function18 = function18;
	}

	public void setFunction19(Long function19) {
		this.function19 = function19;
	}

	public void setFunction20(Long function20) {
		this.function20 = function20;
	}

	public void setFunction21(Long function21) {
		this.function21 = function21;
	}

	public void setFunction22(Long function22) {
		this.function22 = function22;
	}

	public void setFunction23(Long function23) {
		this.function23 = function23;
	}

	public void setFunction24(Long function24) {
		this.function24 = function24;
	}

	public void setFunction25(Long function25) {
		this.function25 = function25;
	}

	public void setFunction26(Long function26) {
		this.function26 = function26;
	}

	public void setFunction27(Long function27) {
		this.function27 = function27;
	}

	public void setFunction28(Long function28) {
		this.function28 = function28;
	}

	public void setFunction29(Long function29) {
		this.function29 = function29;
	}

	public void setFunction30(Long function30) {
		this.function30 = function30;
	}
	
	public void setFunction31(Long function31) {
		this.function31 = function31;
	}
	
	public void setFunction32(Long function32) {
		this.function32 = function32;
	}
	public void setFunction33(Long function33) {
		this.function33 = function33;
	}
	
	public void setFunction34(Long function34) {
		this.function34 = function34;
	}
	
	public void setFunction35(Long function35) {
		this.function35 = function35;
	}	

	public int doStartTag() throws JspException {
		// [[
		int result = 0;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doStartTag");
		}
		// ]]
		try {
			if (function != null) {
				LOG.debug("function " + function);
				if (Authorization.isAllowed(function)) {
					this.authorizedFunction = function;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function2 != null) {
				LOG.debug("function2 " + function2);
				if (Authorization.isAllowed(function2)) {
					this.authorizedFunction = function2;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function3 != null) {
				LOG.debug("function3 " + function3);
				if (Authorization.isAllowed(function3)) {
					this.authorizedFunction = function3;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function4 != null) {
				LOG.debug("function4 " + function4);
				if (Authorization.isAllowed(function4)) {
					this.authorizedFunction = function4;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function5 != null) {
				LOG.debug("function5 " + function5);
				if (Authorization.isAllowed(function5)) {
					this.authorizedFunction = function5;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function6 != null) {
				LOG.debug("function6 " + function6);
				if (Authorization.isAllowed(function6)) {
					this.authorizedFunction = function6;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function7 != null) {
				LOG.debug("function7 " + function7);
				if (Authorization.isAllowed(function7)) {
					this.authorizedFunction = function7;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function8 != null) {
				LOG.debug("function8 " + function8);
				if (Authorization.isAllowed(function8)) {
					this.authorizedFunction = function8;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function9 != null) {
				LOG.debug("function9 " + function9);
				if (Authorization.isAllowed(function9)) {
					this.authorizedFunction = function9;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function10 != null) {
				LOG.debug("function10 " + function10);
				if (Authorization.isAllowed(function10)) {
					this.authorizedFunction = function10;					
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function11 != null) {
				LOG.debug("function11 " + function11);
				if (Authorization.isAllowed(function11)) {
					this.authorizedFunction = function11;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function12 != null) {
				LOG.debug("function12 " + function12);
				if (Authorization.isAllowed(function12)) {
					this.authorizedFunction = function12;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function13 != null) {
				LOG.debug("function13 " + function13);
				if (Authorization.isAllowed(function13)) {
					this.authorizedFunction = function13;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function14 != null) {
				LOG.debug("function14 " + function14);
				if (Authorization.isAllowed(function14)) {
					this.authorizedFunction = function14;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function15 != null) {
				LOG.debug("function15 " + function15);
				if (Authorization.isAllowed(function15)) {
					this.authorizedFunction = function15;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function16 != null) {
				LOG.debug("function16 " + function16);
				if (Authorization.isAllowed(function16)) {
					this.authorizedFunction = function16;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function17 != null) {
				LOG.debug("function17 " + function17);
				if (Authorization.isAllowed(function17)) {
					this.authorizedFunction = function17;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function18 != null) {
				LOG.debug("function18 " + function18);
				if (Authorization.isAllowed(function18)) {
					this.authorizedFunction = function18;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function19 != null) {
				LOG.debug("function19 " + function19);
				if (Authorization.isAllowed(function19)) {
					this.authorizedFunction = function19;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function20 != null) {
				LOG.debug("function20 " + function20);
				if (Authorization.isAllowed(function20)) {
					this.authorizedFunction = function20;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function21 != null) {
				LOG.debug("function21 " + function21);
				if (Authorization.isAllowed(function21)) {
					this.authorizedFunction = function21;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function22 != null) {
				LOG.debug("function22 " + function22);
				if (Authorization.isAllowed(function22)) {
					this.authorizedFunction = function22;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function23 != null) {
				LOG.debug("function23 " + function23);
				this.authorizedFunction = function23;
				if (Authorization.isAllowed(function23)) {
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function24 != null) {
				LOG.debug("function24 " + function24);
				if (Authorization.isAllowed(function24)) {
					this.authorizedFunction = function24;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function25 != null) {
				LOG.debug("function25 " + function25);
				if (Authorization.isAllowed(function25)) {
					this.authorizedFunction = function25;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function26 != null) {
				LOG.debug("function26 " + function26);
				if (Authorization.isAllowed(function26)) {
					this.authorizedFunction = function26;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function27 != null) {
				LOG.debug("function27 " + function27);
				if (Authorization.isAllowed(function27)) {
					this.authorizedFunction = function27;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function28 != null) {
				LOG.debug("function28 " + function28);
				if (Authorization.isAllowed(function28)) {
					this.authorizedFunction = function28;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function29 != null) {
				LOG.debug("function29 " + function29);
				if (Authorization.isAllowed(function29)) {
					this.authorizedFunction = function29;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function30!= null) {
				LOG.debug("function30 " + function30);
				if (Authorization.isAllowed(function30)) {
					this.authorizedFunction = function30;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function31!= null) {
				LOG.debug("function31 " + function31);
				if (Authorization.isAllowed(function31)) {
					this.authorizedFunction = function31;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function32!= null) {
				LOG.debug("function32 " + function32);
				if (Authorization.isAllowed(function32)) {
					this.authorizedFunction = function32;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function33!= null) {
				LOG.debug("function33 " + function33);
				if (Authorization.isAllowed(function33)) {
					this.authorizedFunction = function33;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function34!= null) {
				LOG.debug("function34 " + function34);
				if (Authorization.isAllowed(function34)) {
					this.authorizedFunction = function34;
					return result = EVAL_BODY_INCLUDE;
				}
			}
			if (function35!= null) {
				LOG.debug("function35 " + function35);
				if (Authorization.isAllowed(function35)) {
					this.authorizedFunction = function35;
					return result = EVAL_BODY_INCLUDE;
				}
			}			
			return	result = SKIP_BODY;
				
				
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public Long getAuthorizedFunction() {
		return authorizedFunction;
	}

}
