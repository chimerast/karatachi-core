package org.karatachi.wicket.form.field;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.RangeValidator;

public class InetAddressField extends FormComponentPanel<InetAddress> {
    private static final long serialVersionUID = 1L;

    private FormComponent<Integer> octet1;
    private FormComponent<Integer> octet2;
    private FormComponent<Integer> octet3;
    private FormComponent<Integer> octet4;

    public InetAddressField(String id) {
        super(id);
        commonInit();
    }

    public InetAddressField(String id, IModel<InetAddress> model) {
        super(id, model);
        commonInit();
    }

    private void commonInit() {
        setRenderBodyOnly(true);

        add(octet1 = new TextField<Integer>("octet1", new IpAddressModel(0)));
        add(octet2 = new TextField<Integer>("octet2", new IpAddressModel(1)));
        add(octet3 = new TextField<Integer>("octet3", new IpAddressModel(2)));
        add(octet4 = new TextField<Integer>("octet4", new IpAddressModel(3)));

        IValidator<Integer> validator = new RangeValidator<Integer>(0, 255);
        octet1.add(validator);
        octet2.add(validator);
        octet3.add(validator);
        octet4.add(validator);
    }

    @Override
    protected void convertInput() {
        if (octet1.getConvertedInput() == null
                || octet2.getConvertedInput() == null
                || octet3.getConvertedInput() == null
                || octet4.getConvertedInput() == null) {
            setConvertedInput(null);
            return;
        }

        try {
            InetAddress addr = InetAddressField.this.getModelObject();
            if (addr == null) {
                addr = InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 });
            }
            byte[] octets = addr.getAddress();
            octets[0] = (byte) (0xFF & octet1.getConvertedInput());
            octets[1] = (byte) (0xFF & octet2.getConvertedInput());
            octets[2] = (byte) (0xFF & octet3.getConvertedInput());
            octets[3] = (byte) (0xFF & octet4.getConvertedInput());
            setConvertedInput(InetAddress.getByAddress(octets));
        } catch (UnknownHostException ignore) {
            setConvertedInput(null);
        }
    }

    @Override
    public boolean checkRequired() {
        if (isRequired()) {
            return octet1.getInput() != null && octet2.getInput() != null
                    && octet3.getInput() != null && octet4.getInput() != null;
        }
        return true;
    }

    private class IpAddressModel implements IModel<Integer>,
            IObjectClassAwareModel<Integer> {
        private static final long serialVersionUID = 1L;

        private final int octet;

        public IpAddressModel(int octet) {
            this.octet = octet;
        }

        public void detach() {
        }

        public Integer getObject() {
            InetAddress addr = InetAddressField.this.getModelObject();
            if (addr == null) {
                return null;
            }
            int value = addr.getAddress()[octet];

            return value >= 0 ? value : 256 + value;
        }

        public void setObject(Integer object) {
        }

        public Class<Integer> getObjectClass() {
            return Integer.class;
        }
    }
}
