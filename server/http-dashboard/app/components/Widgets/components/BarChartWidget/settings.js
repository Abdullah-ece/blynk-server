import React from 'react';
import WidgetSettings from '../WidgetSettings';
import PropTypes from 'prop-types';

import {
  reduxForm,
  Field,
  reset,
} from 'redux-form';

import {
  SimpleContentEditable
} from 'components';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

@connect((/*state*/) => ({}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch)
}))
@reduxForm()
class BarChartSettings extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,
    pristine: PropTypes.bool,

    onClose: PropTypes.func,
    onSave: PropTypes.func,
    handleSubmit: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
  }

  labelNameComponent({input}) {
    return (
      <SimpleContentEditable maxLength={35}
                             className="modal-window-widget-settings-config-widget-name"
                             value={input.value} o
                             onChange={input.onChange}/>
    );
  }

  handleCancel() {
    if (typeof this.props.onClose === 'function')
      this.props.onClose();

    // this.props.resetForm(this.props.form);
  }

  handleSave() {
    if(typeof this.props.handleSubmit === 'function')
      this.props.handleSubmit();
  }

  render() {

    return (
      <WidgetSettings
        visible={this.props.visible}
        onSave={this.handleSave}
        onCancel={this.handleCancel}
        isSaveDisabled={this.props.pristine}
        config={(
          <div>
            <div className="modal-window-widget-settings-config-column-header">
              <Field name="label" component={this.labelNameComponent}/>

              <div className="modal-window-widget-settings-config-add-source">
                {/*<Button type="dashed" onClick={this.handleAddSource}>Add source</Button>*/}
              </div>

            </div>
          </div>
        )}

        preview={(
          <div>Preview</div>
        )}
      />
    );
  }

}

export default BarChartSettings;
