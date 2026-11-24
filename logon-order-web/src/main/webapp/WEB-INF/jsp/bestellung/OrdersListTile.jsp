<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrdersListTile.jsp,v $ --%> 

<%@ include file="../includeTLDs.jsp" %>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />

<script type="text/javascript">
	var CustorderExpander = {
		expandedStatus: {
			COLLAPSED: 0,
			LOADING: 1,
			EXPANDED: 2
		},

		custorderStatuses: new Object(),

		getStatus: function(custorderId) {
			var status = this.custorderStatuses[custorderId];

			if (!status) {
				status = this.expandedStatus.COLLAPSED;
			}

			return status;
		},

		setStatus: function(custorderId, status) {
			if (status == this.expandedStatus.COLLAPSED) {
				delete this.custorderStatuses[custorderId];
			}
			else {
				this.custorderStatuses[custorderId] = status;
			}
		},

		toggleShowOrdersForCustorder: function(custorderId) {
			var status = this.getStatus(custorderId);

			if (status != this.expandedStatus.LOADING) {
				var buttons = Ext.query('[id=buttonCustorderExpand' + custorderId + ']');
				var tr = Ext.get('ordersForCustorderTr' + custorderId);
				var div = Ext.get('ordersForCustorderDiv' + custorderId);

				tr.setVisibilityMode(Ext.Element.DISPLAY);

				if (status != this.expandedStatus.EXPANDED) {
					// Show customer order
					this.setStatus(custorderId, this.expandedStatus.LOADING);
					
					for (var j=0; j<buttons.length; j++ ) {
						Ext.get(buttons[j]).removeClass('SmallButtonStyleDown');
						Ext.get(buttons[j]).addClass('SmallButtonStyleUp');
					}

					tr.setVisible(true);

					div.load({
						url: '../bestellung/Bestellung.do',
						params: {
							action: 'showOrdersForCustorder',
							custorderId: custorderId
						},
						text: '<fmt:message key="extjs.loading"/>',
						scripts: true,
						nocache: true,
						callback: this.finishedLoading.createDelegate(this)
					});
				}
				else {
					// Hide customer order
					this.setStatus(custorderId, this.expandedStatus.COLLAPSED);

					for (var j=0; j<buttons.length; j++ ) {
						Ext.get(buttons[j]).removeClass('SmallButtonStyleUp');
						Ext.get(buttons[j]).addClass('SmallButtonStyleDown');
					}
					
					tr.setVisible(false);

					div.select('*').remove();
				}
			}
		},

		finishedLoading: function(el, success, response, options) {
			var custorderId = options.params.custorderId;
			this.setStatus(custorderId, this.expandedStatus.EXPANDED);
		}
	};
</script>

<!-- ------------------------------------- -->
<!-- Open/Completed Orders/Customer Orders -->
<!-- ------------------------------------- -->

	<div id="divOpenCustorders">
		<c:if test="${not empty form.openCustorderList}">
			<jsp:include page="OpenCustorders.jsp"/>
		</c:if>
	</div>

	<div id="divClosedCustorders">
		<c:if test="${not empty form.closedCustorderList}">
			<jsp:include page="ClosedCustorders.jsp"/>
		</c:if>
	</div>

