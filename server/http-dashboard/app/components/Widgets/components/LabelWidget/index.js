import React from 'react';

import LabelWidgetSettings from './settings';

class LabelWidget extends React.Component {

  render() {
    return (
      <div className="widgets--widget-web-label">
        Label widget is comming there
      </div>
    );
  }

}

LabelWidget.Settings = LabelWidgetSettings;

export default LabelWidget;
