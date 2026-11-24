<!-- ---------- -->
<!-- New orders -->
<!-- ---------- -->
<div id="divNewOrders">
	<jsp:include page="NewWebOrders.jsp"/>
</div>

<!-- -------------- -->
<!-- Orgoing orders -->
<!-- -------------- -->
<br>
<div id="divOngoingOrders">
	<jsp:include page="OngoingWebOrders.jsp"/>
</div>
<br>
<!-- ---------------- -->
<!-- Completed orders -->
<!-- ---------------- -->

			<div id="divCompletedOrders">
				<jsp:include page="CompletedWebOrdersTile.jsp"/>
			</div>
