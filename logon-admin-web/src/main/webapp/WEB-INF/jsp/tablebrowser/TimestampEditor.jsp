<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<!-- BEGIN $RCSfile: TimestampEditor.jsp,v $ -->

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">

var storedtime;

Ext.ns('Ext.ux.form');

/*DateTime Picker*/
Ext.DateTimePicker = Ext.extend(Ext.DatePicker, {
	dateFormat:'d.m.Y',
    timeFormat:'H:i.u'
    ,timeLabel:'Time'
    ,timeWidth:100

    ,initComponent:function() {
        Ext.DateTimePicker.superclass.initComponent.call(this);
        this.id = Ext.id();
    }
    
    ,onRender: function(container,position){

        Ext.DateTimePicker.superclass.onRender.apply(this,arguments);
        
        var table = Ext.get(Ext.DomQuery.selectNode('table tbody',container.dom));

        var tfEl = Ext.DomHelper.insertBefore(table.first(),{tag:'tr',
            children:[{tag:'td',cls:'x-date-bottom',html:this.timeLabel},{tag:'td',cls:'x-date-bottom ux-timefield', colspan:'2'}]},true);

        this.tf.render(table.child('td.ux-timefield'));

        if (this.getEl().parent('div.x-layer')){
            var zindex = this.getEl().parent('div.x-layer').getStyle('z-index');
            if(this.tf.list)
                this.tf.list.setStyle('z-index',this.getEl().parent('div.x-layer').getStyle('z-index')+ 1000);
        }
    }
    
    ,setValue : function(value){
        var old = this.value;
        if (!this.tf){
            // create timefield
            var timeConfig = Ext.apply({}, {
                 id:this.id + '-time'
                ,format:this.timeFormat || Ext.form.TimeField.prototype.format
                ,width:this.timeWidth
                ,fieldLabel:this.timeLabel
                ,selectOnFocus:true
                ,lazyInit: false
            }, this.timeConfig);
            this.tf = new Ext.form.TimeField(timeConfig);
            this.tf.ownerCt = this;
            delete(this.timeFormat);
            this.tf.setValue(value);
        }
        
        this.value = this.getDateTime(value);

        if(this.el){
            this.update(this.value);
        }

    }

    ,getDateTime: function(value){
        if (this.tf){
            var dt = new Date();
            var timeval = this.tf.getValue();
            value = Date.parseDate(value.format(this.dateFormat) + ' ' +timeval,this.format);
        }
        return value;
    }
    
    ,selectToday : function(){
        if(this.todayBtn && !this.todayBtn.disabled){
            this.value=this.getDateTime(new Date());
            this.fireEvent("select", this, this.value);
        }
    }
});
Ext.reg('datetimepickerfield', Ext.DateTimePicker);

/*DateTime Item Menu*/
Ext.menu.DateTimeItem = function(config){
    Ext.menu.DateTimeItem.superclass.constructor.call(this, new Ext.DateTimePicker(config), config);
    this.picker = this.component;
    this.addEvents('select');
    
    this.picker.on("render", function(picker){
        picker.getEl().swallowEvent("click");
        picker.container.addClass("x-menu-date-item");
    });

    this.picker.on("select", this.onSelect, this);
};

Ext.extend(Ext.menu.DateTimeItem, Ext.menu.Adapter, {
    onSelect : function(picker, date){
        this.fireEvent("select", this, date, picker);
        Ext.menu.DateTimeItem.superclass.handleClick.call(this);
    }
});

/*DateTime Menu*/
Ext.menu.DateTimeMenu = function(config){
    Ext.menu.DateTimeMenu.superclass.constructor.call(this, config);
    this.plain = true;
    var di = new Ext.menu.DateTimeItem(config);
    this.add(di);
    this.picker = di.picker;
    this.relayEvents(di, ["select"]);

    this.on('beforeshow', function(){
        if(this.picker){
            this.picker.hideMonthPicker(true);
        }
    }, this);
};
Ext.extend(Ext.menu.DateTimeMenu, Ext.menu.Menu, {
    cls:'x-date-menu',
    beforeDestroy : function() {
        this.picker.destroy();
    }
    ,hide : function(deep){
        if (this.picker.tf.innerList){
            if ((Ext.EventObject.within(this.picker.tf.innerList)) || (Ext.get(Ext.EventObject.getTarget())==this.picker.tf.innerList))
                return false;
        }
        if(this.el && this.isVisible()){
            this.fireEvent("beforehide", this);
            if(this.activeItem){
                this.activeItem.deactivate();
                this.activeItem = null;
            }
            this.el.hide();
            this.hidden = true;
            this.fireEvent("hide", this);
        }
        if(deep === true && this.parentMenu){
            this.parentMenu.hide(true);
        }
    }
});

/*DateTime Field*/
Ext.ux.form.DateTimeField = Ext.extend(Ext.form.DateField, {
    
    dateFormat: 'd.m.Y'
    ,timeFormat: 'H:i.d'

    ,defaultAutoCreate : {tag: "input", type: "text", size: "20", autocomplete: "off"}
    
    ,initComponent:function() {
        Ext.ux.form.DateTimeField.superclass.initComponent.call(this);
        this.format = this.dateFormat + ' ' + this.timeFormat;
        this.afterMethod('afterRender',function(){
            this.getEl().applyStyles('top:0');
        });
    }
    
    ,getValue : function(){
        return this.parseDate(Ext.form.DateField.superclass.getValue.call(this)) || '';
    }
    
    ,onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.menu == null){
            this.menu = new Ext.menu.DateTimeMenu();
        }
        Ext.apply(this.menu.picker,  {
            minDate : this.minValue,
            maxDate : this.maxValue,
            disabledDatesRE : this.ddMatch,
            disabledDatesText : this.disabledDatesText,
            disabledDays : this.disabledDays,
            disabledDaysText : this.disabledDaysText,
            format : this.format,
            timeFormat: this.timeFormat,
            dateFormat: this.dateFormat,
            showToday : this.showToday,
            minText : String.format(this.minText, this.formatDate(this.minValue)),
            maxText : String.format(this.maxText, this.formatDate(this.maxValue))
        });
        this.menu.on(Ext.apply({}, this.menuListeners, {
            scope:this
        }));
        this.menu.picker.setValue(this.getValue() || new Date());
        this.menu.show(this.el, "tl-bl?");
    }
});
Ext.reg('datetimefield', Ext.ux.form.DateTimeField);
    
  // Ext.onReady(function(){
  //// prepare date picker
  //   var picker = new Ext.form.DateField({
  //       applyTo: 'extdatepicker<c:out value="${valueNr}" />',
  //       fieldLabel: 'date',
  //       format: 'y-m-d H:i:s:u',
  //       allowBlank: true,
  //  });
  //   picker.on('select', function(dp, date){
  //		alert('onselect');       
  //  });  
  //});
 
 Ext.onReady(function(){
  // prepare date picker
     var picker = new Ext.ux.form.DateTimeField({
         applyTo: 'extdatepicker<c:out value="${valueNr}" />',
         fieldLabel: 'date',
         dateFormat: 'd.m.Y',
         timeFormat: 'H:i:s.u',
         allowBlank: true
     });
 });
 
</script>

<html:text property="value[${valueNr}]" styleId="extdatepicker${valueNr}" value="${value.webValue}"/>