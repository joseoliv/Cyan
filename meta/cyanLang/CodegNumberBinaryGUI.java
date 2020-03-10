package meta.cyanLang;

import java.awt.Component;
import java.awt.Toolkit;

/**
 *
 * Codeg Library
 *
 * @author André Cassulino Araújo Souza
 *
 */
public class CodegNumberBinaryGUI extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	byte[] userInput;
	private String label;
	private boolean formClick = false;
	private int mouseCount = 0;

	public CodegNumberBinaryGUI(java.awt.Frame parent, byte[] previousUserInput, String previousLabel) {
		super(parent, true);
		userInput = previousUserInput;
		label = previousLabel;
		initComponents();
		setLocation(((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (this.getWidth() / 2)),
				((Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (this.getHeight() / 2)));

		for (Component c : this.getComponents()) {
			c.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent evt) {
					formClicked();
				}

				@Override
				public void mouseExited(java.awt.event.MouseEvent evt) {
					formExited(false);
				}

				@Override
				public void mousePressed(java.awt.event.MouseEvent evt) {
					formClicked();
				}
			});
		}

		if (userInput == null) {
			userInput = "".getBytes();
		}
	}

	private void initComponents() {

		btOk = new javax.swing.JButton();
		btCancel = new javax.swing.JButton();
		lblBinary = new javax.swing.JLabel();
		txtBinary = new javax.swing.JTextField();
		lblDecimal = new javax.swing.JLabel();
		txtDecimal = new javax.swing.JTextField();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Codeg Binary Number");
		setResizable(false);
		setAlwaysOnTop(true);
		setModal(true);
		setType(java.awt.Window.Type.POPUP);
		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				formClicked();
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				formExited(true);
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				formClicked();
			}
		});

		btOk.setText("Ok");
		btOk.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btOkActionPerformed(evt);
			}
		});

		btCancel.setText("Cancel");
		btCancel.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btCancelActionPerformed(evt);
			}
		});

		lblBinary.setText("Binary Number:");

		txtBinary.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				txtBinaryKeyReleased(evt);
			}
		});

		lblDecimal.setText("Decimal Number:");

		txtDecimal.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				txtDecimalKeyReleased(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
						.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(lblBinary).addComponent(txtBinary, javax.swing.GroupLayout.PREFERRED_SIZE,
										200, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(0, 0, Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
								.addGap(0, 0, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup().addComponent(btOk)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(btCancel))
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(lblDecimal).addComponent(txtDecimal,
																javax.swing.GroupLayout.PREFERRED_SIZE, 200,
																javax.swing.GroupLayout.PREFERRED_SIZE)))))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(lblBinary)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtBinary, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblDecimal).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtDecimal, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btCancel).addComponent(btOk))
						.addContainerGap()));

		btOk.getAccessibleContext().setAccessibleDescription("");

		pack();
	}

	private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void btOkActionPerformed(java.awt.event.ActionEvent evt) {
		userInput = txtBinary.getText().getBytes();
		this.dispose();
	}

	private void formClicked() {
		formClick = true;
	}

	private void formExited(Boolean frm) {
		if (!formClick && mouseCount < -3) {
			this.dispose();
		} else {
			if (frm) {
				mouseCount++;
			} else {
				mouseCount--;
			}
		}
	}

	private void txtBinaryKeyReleased(java.awt.event.KeyEvent evt) {
		String num = txtBinary.getText();
		if (!(num.matches("[01]+") && !num.startsWith("0")) && num.length() > 0) {
			System.out.println("Enter a binary number!");
			txtDecimal.setText("");
		} else {
			Integer numero = Integer.parseInt(num, 2);
			txtDecimal.setText(numero.toString());
		}
	}

	private void txtDecimalKeyReleased(java.awt.event.KeyEvent evt) {
		String num = txtDecimal.getText();
		try {
			Integer numero = Integer.parseInt(num);
			txtBinary.setText(Integer.toString(numero, 2));
		} catch (Exception e) {
			System.out.println("Enter a decimal number!");
			txtBinary.setText("");
		}
	}

	private javax.swing.JButton btCancel;
	private javax.swing.JButton btOk;
	private javax.swing.JLabel lblBinary;
	private javax.swing.JLabel lblDecimal;
	private javax.swing.JTextField txtBinary;
	private javax.swing.JTextField txtDecimal;

	public byte[] getUserInput() {
		return userInput;
	}

	public String getLabel() {
		return label;
	}
}
